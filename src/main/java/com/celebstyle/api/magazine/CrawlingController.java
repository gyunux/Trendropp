package com.celebstyle.api.magazine;

import com.celebstyle.api.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

//향후 스케줄러로 자동 수집 관리
@RestController
@RequiredArgsConstructor
public class CrawlingController {
    private final ArticleService articleService;

    @PostMapping("/api/myCrawl/vivienne")
    public String startCrawling(){
        articleService.saveCrawledArticles();
        return "Crawling and data saving initiated.";
    }
}
