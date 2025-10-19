package com.celebstyle.api.magazine.service.impl;

import com.celebstyle.api.config.GeminiProperties;
import com.celebstyle.api.magazine.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService implements AiService {

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public String getSummary(String title,String body){
        if(body == null || body.isEmpty()){
            return "본문 내용이 없어 요약할 수 없습니다";
        }

        String prompt = String.format(
                """     
                        내가 지금 주는 제목과 본문은 패션에 관한 기사야 너는 패션관련 콘텐츠 전문가야
                        다음 기사 제목과 본문을 바탕으로 핵심 내용만 1문장으로 간결하게 요약해줘.\
                        불필요한 수식어는 제거하고, 간결한 사실 전달에 중점을 두고.\s
                        착용하고 있는 아이템에 대한 설명은 간결하게만 해줘.
                        너무 딱딱한 말투로는 쓰지말고 실제 매거진 이나 패션 콘텐츠 전문가가 쓰는 말투로 사용해서 작성해줘
                        그리고 꼭 한국어로 돌려줘야해 나는 한국인이고 한국어만 지원할거야
                        "제목 : %s
                        "본문 : %s"
                        """,title,body.substring(0,Math.min(body.length(), 4000))
        );
        String requestBodyJson = createRequestBody(prompt);

        try{
            String url = GEMINI_API_URL + geminiProperties.getKey();
            String responseJson = restTemplate.postForObject(url,requestBodyJson,String.class);

            return parseSummaryFromResponse(responseJson);
        } catch(Exception e){
            log.error("Gemini API 호출 실패 : {}",e.getMessage());
            return "AI 요약 실패 : API 통신 오류";
        }
    }

    private String createRequestBody(String prompt) {
        String escapedPrompt = prompt.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        // temperature 설정을 추가한 JSON 형식
        return String.format("""
    {
      "contents": [
        {
          "parts": [
            {
              "text": "%s"
            }
          ]
        }
      ],
      "generationConfig": {
        "temperature": 0.8
      }
    }
    """, escapedPrompt);
    }

    private String parseSummaryFromResponse(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);

        JsonNode summaryNode = root
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text");

        if (summaryNode.isTextual()) {
            return summaryNode.asText().trim();
        }

        log.warn("Gemini 응답 구조 오류: {}", responseJson);
        return "AI 요약 실패: 유효하지 않은 응답";
    }

}
