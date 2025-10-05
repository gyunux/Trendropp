package com.celebstyle.api.article;

import com.celebstyle.api.magazine.CrawlerDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import java.time.LocalDate;
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

    private String title;

    private String articleUrl;

    //향후 별도 클래스 일대다 관계로 분리?
    @ElementCollection
    @CollectionTable(name = "article_images",joinColumns = @JoinColumn(name = "article_img_id"))
    private List<String> imageUrls;

    private String source;

    @Lob
    private String body;

    private boolean processed;

    private LocalDate articleDate;

    public Article(CrawlerDto dto) {
        this.title = dto.getTitle();
        this.articleUrl = dto.getArticleUrl();
        this.imageUrls = dto.getImageUrls();
        this.source = dto.getSource();
        this.body = dto.getBody();
        this.processed = false;
        this.articleDate = dto.getArticleDate();
    }
}