package com.celebstyle.api.article.repository;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.dto.ArticleAdminView;
import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticleAdminView> findArticleAdminViews();

    Article findArticleCreateView(Long id);
}
