package com.celebstyle.api.magazine;


import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import com.celebstyle.api.common.S3UploadService;
import com.celebstyle.api.magazine.service.AiService;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class VogueCelebStyleCrawler implements MagazineCrawler {
    private static final String VOGUE_CELEB_STYLE_URL = "https://www.vogue.co.kr/fashion/celebrity-style/";
    private static final String SOURCE_NAME = "Vogue Celeb Style";
    private final CelebRepository celebRepository;
    private final AiService aiService;
    private final S3UploadService s3UploadService;

    @Value("${crawler.repeat-count}")
    private int repeatCount;

    @Override
    public List<CrawlerDto> crawl() throws IOException {
        log.info("크롤링 시작");
        //크롬 동적 크롤링 코드 시작
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-extensions");
        options.addArguments("--window-size=1920,1200");
        options.addArguments("--ignore-certificate-errors");

        WebDriver driver = new ChromeDriver(options);
        List<CrawlerDto> newsList = new ArrayList<>();

        try {
            List<String> koreanCelebNames = celebRepository.findAll().stream()
                    .map(Celeb::getName)
                    .toList();

            if (koreanCelebNames.isEmpty()) {
                log.warn("DB에 셀럽 정보가 없습니다. 필터링을 진행할 수 없습니다.");
                return Collections.emptyList();
            }

            List<String> detailUrls = getArticleDetailUrls(driver);

            for (String url : detailUrls) {
                try {
                    CrawlerDto news = scrapeDetailPage(driver, url, koreanCelebNames);
                    if (news != null) {

                        List<String> s3ImageUrls = new ArrayList<>();
                        for (String originalUrl : news.getImageUrls()) {
                            try {
                                String s3Url = s3UploadService.uploadFromUrl(originalUrl, "articles");
                                s3ImageUrls.add(s3Url);
                            } catch (Exception e) {
                                log.error("S3 개별 이미지 업로드 실패 (스킵): {}", originalUrl, e);
                            }
                        }
                        news.setImageUrls(s3ImageUrls);

                        newsList.add(news);
                    }
                } catch (Exception e) {
                    log.error("Error Crawling detail page: {}, {}", url, e.getMessage());
                }
            }
            log.info("AI 요약 서비스 시작: 수집된 유효 기사 {}개", newsList.size());

            for (CrawlerDto news : newsList) {
                try {
                    // AI에게 보낼 본문 전체를 변수에 저장
                    String fullBodyForAI = news.getBody().substring(0, Math.min(news.getBody().length(), 4000));

                    try (java.io.PrintWriter out = new java.io.PrintWriter("debug_body.txt")) {
                        out.println("--- TITLE ---");
                        out.println(news.getTitle());
                        out.println("\n--- BODY ---");
                        out.println(fullBodyForAI);
                    }

                    // 이제 AI에게 요약 요청
                    String summary = aiService.getSummary(news.getTitle(), fullBodyForAI);
                    news.setSummary(summary);
                    log.info("요약 완료: {}", news.getTitle());

                } catch (Exception e) {
                    log.error("AI 요약 중 오류 발생 (기사: {}): {}", news.getTitle(), e.getMessage());
                    news.setSummary("AI 요약 실패");
                }
            }
            log.info("AI 요약 서비스 완료.");

        } finally {
            log.info("크롤링 완료. WebDriver를 종료합니다.");
            driver.quit(); // 에러 발생 여부와 상관없이 항상 드라이버 종료
        }
        return newsList;
    }

    private List<String> getArticleDetailUrls(WebDriver driver) { // IOException 제거 가능
        driver.get(VOGUE_CELEB_STYLE_URL);
        Set<String> urlSet = new HashSet<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait initialWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 처음 페이지 로드 시 기다림
            initialWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.list_group li a")));

            for (int i = 0; i < repeatCount; i++) {

                // 현재 페이지 URL 수집
                Document doc = Jsoup.parse(driver.getPageSource(), VOGUE_CELEB_STYLE_URL);
                doc.select("div.list_group li > a").stream()
                        .map(element -> element.attr("abs:href"))
                        .filter(StringUtils::hasText)
                        .forEach(urlSet::add);

                int currentUrlCount = urlSet.size();

                // 페이지 맨 아래로 스크롤
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

                // [핵심 수정] WebDriverWait 대신 Thread.sleep 사용
                try {
                    // 스크롤 후 새로운 콘텐츠가 로드될 시간을 3초간 강제로 부여합니다.
                    // 네트워크 상태나 PC 성능에 따라 이 시간을 조절해야 할 수 있습니다.
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("스크롤 대기 중 인터럽트 발생");
                    break;
                }

                if (urlSet.size() == currentUrlCount) {
                    log.info("새로운 기사가 더 이상 로드되지 않아 스크롤을 중단합니다. ({}회 실행)", i + 1);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("기사 목록 URL 수집 중 오류 발생", e);
            return Collections.emptyList();
        }

        log.info("총 {}개의 고유한 기사 링크를 수집했습니다.", urlSet.size());
        return new ArrayList<>(urlSet);
    }


    private CrawlerDto scrapeDetailPage(WebDriver driver,String url,List<String> koreanCelebNames) throws IOException {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver; // JavascriptExecutor 추가

        try {
            // 1. 주요 콘텐츠 영역 기다리기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.contt")));

            // [핵심 수정] 2. 페이지 내 모든 관련 이미지를 찾아서 하나씩 스크롤하여 로딩 유도
            List<WebElement> images = driver.findElements(By.cssSelector("figure.wp-block-image img"));
            for (WebElement img : images) {
                try {
                    // 이미지를 뷰포트로 스크롤 (true는 화면 상단에 맞춤)
                    js.executeScript("arguments[0].scrollIntoView(true);", img);
                    // 스크롤 후 이미지 로딩을 위한 아주 짧은 대기
                    Thread.sleep(200);
                } catch (Exception scrollEx) {
                    log.warn("개별 이미지 스크롤 중 오류 발생 (무시): {}", scrollEx.getMessage());
                }
            }

            // [추가] 모든 스크롤 후 최종 렌더링을 위해 잠시 대기
            Thread.sleep(1000);

        } catch (Exception e) {
            log.error("상세 페이지 로딩 또는 이미지 스크롤 중 오류 (스킵): {}", url, e);
            return null;
        }
        Document doc = Jsoup.parse(driver.getPageSource());
        Element titleElement = doc.getElementsByClass("post_tit pc").first();
        String title = (titleElement != null) ? titleElement.text() : "제목 없음";

        LocalDate articleDate = null;
        try {
            // 1. 날짜가 포함된 <p class="date"> 요소 선택
            Element dateElement = doc.select("p.date").first();
            if (dateElement != null) {
                // 2. "2025.10.02 by 하솔휘" 같은 텍스트에서 날짜 부분만 추출
                String dateString = dateElement.ownText().trim();
                // 3. "yyyy.MM.dd" 형식에 맞게 LocalDate 객체로 변환
                articleDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
        } catch (Exception e) {
            log.warn("날짜 파싱 중 에러 발생, URL: {}. 오늘 날짜로 대체합니다.", url);
            articleDate = LocalDate.now(); // 파싱 실패 시 오늘 날짜로 저장
        }

        boolean isDomesticCelebArticle = koreanCelebNames.stream()
                .anyMatch(title::contains);

        if (!isDomesticCelebArticle) {
            return null;
        }

        Set<String> imageUrlSet = new HashSet<>();
        Elements images = doc.select("figure.wp-block-image img");
        for(Element img : images){
            String imgUrl = img.attr("data-src");
            if(imgUrl.isEmpty()){
                imgUrl = img.attr("src");
            }
            imageUrlSet.add(imgUrl);
        }
        log.info(String.valueOf(imageUrlSet.size()));

        Elements bodies = doc.select("div.contt p:not(.relate_group p)");
        String body = bodies.text();

        List<String> imageUrls = new ArrayList<>(imageUrlSet);

        log.info("국내 연예인 기사 수집: {}", title);
        if (!title.equals("제목 없음") && !url.isEmpty() && !imageUrls.isEmpty()) {
            return new CrawlerDto(title, url, imageUrls, getSource(), body,articleDate,null);
        }
        return null;
    }

    @Override
    public String getSource () {
        return SOURCE_NAME;
    }
}
