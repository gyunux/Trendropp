package com.celebstyle.api.magazine;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.ArticleRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingService {

    private final MagazineCrawler magazineCrawler;
    private final ArticleRepository articleRepository;

    @Transactional
    public void saveCrawledArticles(){
        try{
            List<CrawlerDto> dtos = magazineCrawler.crawl();

            int newArticleCount = 0;

            for(CrawlerDto dto : dtos){
                if(!articleRepository.existsByArticleUrl(dto.getArticleUrl())){
                    Article article = new Article(dto);
                    articleRepository.save(article);
                    newArticleCount++;
                }
            }
            log.info("새로운 기사 {}개가 성공적으로 저장되었습니다.",newArticleCount);
        } catch(IOException e){
            log.error("크롤링 중 에러 발생: {}",e.getMessage());
        }
    }
}
