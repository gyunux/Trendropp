package com.celebstyle.api.article.dto;

import com.celebstyle.api.article.Article;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleAdminView {
    private Long id;
    private LocalDate articleDate;
    private String thumbnailImageUrl;
    private String title;
    private String articleUrl;
    private boolean processed;


    public static ArticleAdminView fromEntity(Article article) {
        return new ArticleAdminView(
                article.getId(),
                article.getArticleDate(),
                null,
                article.getTitleKo(),
                article.getArticleUrl(),
                article.isProcessed());
    }
}
