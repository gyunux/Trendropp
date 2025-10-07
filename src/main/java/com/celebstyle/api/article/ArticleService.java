package com.celebstyle.api.article;

import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.magazine.CrawlerDto;
import com.celebstyle.api.magazine.MagazineCrawler;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private final MagazineCrawler magazineCrawler;
    private final ArticleRepository articleRepository;


    //크롤링해온 데이터를 기사 엔티티에 저장
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

    @Transactional(readOnly = true)
    public List<ArticleAdminView> findAllForAdminView(){
        return articleRepository.findAllByOrderByArticleDateDesc().stream()
                .map(ArticleAdminView::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Article findById(Long id){
        return articleRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("해당 ID의 셀럽을 찾을 수 없습니다: " + id);
        }
        articleRepository.deleteById(id);
    }

}
