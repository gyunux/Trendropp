package com.celebstyle.api.x;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class XService {

    private final WebClient xWebClient;

    @Value("${x-api.max-result}")
    private int maxResult;

    public void searchTweets(String query) {
        try {
            XSearchResponse response = xWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/" + query + "/tweets")
//                            .queryParam("max_results", maxResult) 이거 찾아봐야겠다
                            .build())
                    .retrieve()
                    .bodyToMono(XSearchResponse.class)
                    .block();
            if (response != null) {
//                for (XTweet tweet : response.data()) {
//                    log.info("ID: {},Text: {}", tweet.id(), tweet.text());
//                }
                log.info("Text: {}", response);
            } else {
                log.info("검색 결과가 없습니다.");
            }
        } catch (WebClientResponseException e) {
            log.error("API 호출 에러: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 429) {
                String resetHeader = e.getHeaders().getFirst("x-rate-limit-reset");

                if (resetHeader != null) {
                    long resetEpoch = Long.parseLong(resetHeader);
                    long currentEpoch = System.currentTimeMillis() / 1000;
                    long waitSeconds = resetEpoch - currentEpoch;

                    long minutes = waitSeconds / 60;
                    long seconds = waitSeconds % 60;

                    String resetTimeStr = Instant.ofEpochSecond(resetEpoch)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("HH시 mm분 ss초"));

                    log.error("🚨 쿼터 초과! (429 Too Many Requests)");
                    log.error("⏳ 풀리는 시간: {} (약 {}분 {}초 남음)", resetTimeStr, minutes, seconds);
                } else {
                    log.error("🚨 쿼터 초과! (헤더 없음, 15분 대기 추천)");
                }
            } else {
                log.error("API 호출 에러: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            }
        }
    }

}
