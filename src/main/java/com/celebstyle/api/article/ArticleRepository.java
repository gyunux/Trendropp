package com.celebstyle.api.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    boolean existsByArticleUrl(String articleUrl);
}
