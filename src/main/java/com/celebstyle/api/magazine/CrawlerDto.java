package com.celebstyle.api.magazine;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Setter
public class CrawlerDto {
    private String title;
    private String articleUrl;
    private List<String> imageUrls;
    private String source;
    private String body;
    private LocalDate articleDate;
    private String summary;
}
