package com.celebstyle.api.article.service;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.ArticleImage;
import com.celebstyle.api.article.dto.ArticleAdminView;
import com.celebstyle.api.article.repository.ArticleRepository;
import com.celebstyle.api.article.repository.ArticleRepositoryCustom;
import com.celebstyle.api.magazine.MagazineCrawler;
import com.celebstyle.api.magazine.dto.CrawlerDto;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private final MagazineCrawler magazineCrawler;
    private final ArticleRepository articleRepository;

    private final ArticleRepositoryCustom articleRepositoryCustom;

    //크롤링해온 데이터를 기사 엔티티에 저장
    @Transactional
    public void saveCrawledArticles() {
        try {
            List<CrawlerDto> dtos = magazineCrawler.crawl();

            int newArticleCount = 0;

            for (CrawlerDto dto : dtos) {
                if (!articleRepository.existsByArticleUrl(dto.getArticleUrl())) {
                    Article newArticle = new Article(dto);
                    for (String url : dto.getImageUrls()) {
                        ArticleImage articleImage = new ArticleImage(url);
                        newArticle.addArticleImage(articleImage);
                    }
                    articleRepository.save(newArticle);
                    newArticleCount++;
                }
            }
            log.info("새로운 기사 {}개가 성공적으로 저장되었습니다.", newArticleCount);
        } catch (IOException e) {
            log.error("크롤링 중 에러 발생: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<ArticleAdminView> findAllForAdminView(Pageable pageable) {
        return articleRepositoryCustom.findArticleAdminViews(pageable);
    }

    @Transactional(readOnly = true)
    public Article findById(Long id) {
        Article article = articleRepositoryCustom.findArticleCreateView(id);
        if (article == null) {
            throw new EntityNotFoundException();
        }

        return article;
    }

    @Transactional
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("해당 ID의 셀럽을 찾을 수 없습니다: " + id);
        }
        articleRepository.deleteById(id);
    }

}
