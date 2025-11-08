package com.celebstyle.api.magazine;

import com.celebstyle.api.magazine.dto.CrawlerDto;
import java.io.IOException;
import java.util.List;

public interface MagazineCrawler {
    List<CrawlerDto> crawl() throws IOException;

    String getSource();
}
