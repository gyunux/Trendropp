package com.celebstyle.api.article;

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

    public static ArticleAdminView fromEntity(Article article){
        return new ArticleAdminView(
                article.getId(),
                article.getArticleDate(),
                article.getImageUrls().getFirst(),
                article.getTitle(),
                article.getArticleUrl(),
                article.isProcessed());
    }
}
