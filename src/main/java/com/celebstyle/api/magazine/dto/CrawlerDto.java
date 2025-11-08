package com.celebstyle.api.magazine.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
@NoArgsConstructor
public class CrawlerDto {
    private String articleUrl;
    private List<String> imageUrls;
    private String source;
    private String body;
    private LocalDate articleDate;

    private String titleKo;
    private String titleEn;
    private String summaryKo;
    private String summaryEn;

    public CrawlerDto(String articleUrl, List<String> imageUrls, String source, String body, LocalDate articleDate,
                      String originalTitleKo) {
        this.articleUrl = articleUrl;
        this.imageUrls = imageUrls;
        this.source = source;
        this.body = body;
        this.articleDate = articleDate;
        this.titleKo = originalTitleKo;

    }
}
