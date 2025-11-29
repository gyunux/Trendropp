package com.celebstyle.api.x;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/x")
@RequiredArgsConstructor
public class XController {

    private final XService xService;

    @GetMapping("/search")
    public String search(@RequestParam String query) {
        xService.searchTweets(query);

        return "호출 완료! 콘솔 로그를 확인하세요. (검색어: " + query + ")";
    }
}
