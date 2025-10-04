package com.celebstyle.api.magazine;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CrawlerDto {
    private String title;
    private String articleUrl;
    private List<String> imageUrls;
    private String source;
    private String body;
}
