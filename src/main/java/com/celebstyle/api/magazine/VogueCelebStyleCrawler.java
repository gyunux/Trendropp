package com.celebstyle.api.magazine;


import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class VogueCelebStyleCrawler implements MagazineCrawler {
    private static final String VOGUE_CELEB_STYLE_URL = "https://www.vogue.co.kr/fashion/celebrity-style/";
    private static final String SOURCE_NAME = "Vogue Celeb Style";
    private final CelebRepository celebRepository;


    public VogueCelebStyleCrawler(CelebRepository celebRepository) {
        this.celebRepository = celebRepository;
    }

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
                        newsList.add(news);
                    }
                    Thread.sleep(1000); // 페이지 간 요청 간격
                } catch (Exception e) {
                    log.error("Error Crawling detail page: {}, {}", url, e.getMessage());
                }
            }

        } finally {
            log.info("크롤링 완료. WebDriver를 종료합니다.");
            driver.quit(); // 에러 발생 여부와 상관없이 항상 드라이버 종료
        }
        return newsList;
    }

    private List<String> getArticleDetailUrls(WebDriver driver) throws IOException {
        driver.get(VOGUE_CELEB_STYLE_URL);

        Set<String> urlSet = new HashSet<>();

        JavascriptExecutor js = (JavascriptExecutor) driver;

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.list_group li a")));

        for(int i = 0;i < 50; i++){
            Document doc = Jsoup.parse(driver.getPageSource(),VOGUE_CELEB_STYLE_URL);
            Elements linkElements = doc.select("div.list_group li > a");
            linkElements.stream()
                    .map(element -> element.attr("abs:href"))
                    .filter(StringUtils::hasText)
                    .forEach(urlSet::add);

            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            log.info("스크롤 다운 실행...");

            try {
                Thread.sleep(2000); // 2초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        log.info("총 {}개의 고유한 기사 링크를 수집했습니다.", urlSet.size());
        return new ArrayList<>(urlSet);
    }

    private CrawlerDto scrapeDetailPage(WebDriver driver,String url,List<String> koreanCelebNames) throws IOException {

        driver.get(url);

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.contt")));

        //기사 타이틀 가져오기
        Document doc = Jsoup.parse(driver.getPageSource());
        Element titleElement = doc.getElementsByClass("post_tit pc").first();
        String title = (titleElement != null) ? titleElement.text() : "제목 없음";

        log.info("--- 필터링 체크 --- 기사 제목: \"{}\"", title);


        boolean isDomesticCelebArticle = koreanCelebNames.stream()
                .anyMatch(title::contains);

        if (!isDomesticCelebArticle) {
            return null;
        }

        List<String> imageUrls = new ArrayList<>();
        Elements images = doc.select("figure.wp-block-image img");
        for(Element img : images){
            String imgUrl = img.attr("data-src");
            if(imgUrl.isEmpty()){
                imgUrl = img.attr("src");
            }
            imageUrls.add(imgUrl);
        }
        System.out.println(imageUrls.size());

        Elements bodies = doc.select("div.contt p:not(.relate_group p)");
        String body = bodies.toString();

        log.info("국내 연예인 기사 수집: {}", title);
        if (!title.equals("제목 없음") && !url.isEmpty() && !imageUrls.isEmpty()) {
            return new CrawlerDto(title, url, imageUrls, getSource(), body);
        }
        return null;
    }

    @Override
    public String getSource () {
        return SOURCE_NAME;
    }
}
