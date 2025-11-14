package com.celebstyle.api.article.repository;

import com.celebstyle.api.article.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByArticleUrl(String articleUrl);

    //    @Query("SELECT * FROM article a")
    List<Article> findAllByOrderByArticleDateDesc();

}
