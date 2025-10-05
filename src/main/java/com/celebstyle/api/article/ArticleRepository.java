package com.celebstyle.api.article;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    boolean existsByArticleUrl(String articleUrl);
    List<Article> findAllByOrderByArticleDateDesc();
}
