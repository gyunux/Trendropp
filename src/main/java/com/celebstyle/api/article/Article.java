package com.celebstyle.api.article;

import com.celebstyle.api.magazine.dto.CrawlerDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titleKo;

    private String titleEn;

    private String articleUrl;

    private String source;

    @Lob
    private String body;

    private boolean processed;

    private LocalDate articleDate;

    @Lob
    private String summaryKo;

    @Lob
    private String summaryEn;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleImage> articleImages = new ArrayList<>();

    public Article(CrawlerDto dto) {
        this.titleKo = dto.getTitleKo();
        this.titleEn = dto.getTitleEn();
        this.articleUrl = dto.getArticleUrl();
        this.source = dto.getSource();
        this.body = dto.getBody();
        this.processed = false;
        this.articleDate = dto.getArticleDate();
        this.summaryKo = dto.getSummaryKo();
        this.summaryEn = dto.getSummaryEn();

    }

    public void markAsProcessed() {
        this.processed = true;
    }

    public void addArticleImage(ArticleImage articleImage) {
        this.articleImages.add(articleImage);
        articleImage.setArticle(this);
    }
}