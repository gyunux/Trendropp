package com.celebstyle.api.article.repository;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.dto.ArticleAdminView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {
    Page<ArticleAdminView> findArticleAdminViews(Pageable pageable);

    Article findArticleCreateView(Long id);
}
