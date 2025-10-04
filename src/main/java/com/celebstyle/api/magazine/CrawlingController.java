package com.celebstyle.api.magazine;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {
    private final CrawlingService crawlingService;

    @PostMapping("/api/crawl")
    public String startCrawling(){
        crawlingService.saveCrawledArticles();
        return "Crawling and data saving initiated.";
    }
}
