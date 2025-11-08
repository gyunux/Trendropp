package com.celebstyle.api.magazine.service.impl;

import com.celebstyle.api.common.config.GeminiProperties;
import com.celebstyle.api.magazine.dto.AiTranslationResponse;
import com.celebstyle.api.magazine.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public AiTranslationResponse getSummariesAndTranslations(String originalTitle, String originalBody) {
        if (originalTitle == null || originalBody.isEmpty()) {
            return null;
        }

        String prompt = """     
                당신은 10년 차 패션 매거진 에디터이자 전문 번역가입니다.
                당신의 임무는 주어진 한국어 기사 원문을 바탕으로, SEO에 최적화된 새로운 제목과 요약문을 "한국어"와 "영어"로 각각 생성하는 것입니다.
                
                # 지시 사항:
                1.  제목 (title): 원문 제목을 그대로 쓰지 말고, 유저의 흥미를 유발할 수 있는 "후킹한" 키워드를 사용하여 SEO에 최적화된 새로운 제목을 생성합니다. (예: "제니 세상핫한 공항룩")
                2.  요약 (summary): 원문 본문을 3~4 문장의 문단으로 요약합니다.
                    스타일: 유저가 "쉽고, 감성적으로" 읽을 수 있는 어조를 사용합니다.
                    핵심 정보 포함: 요약문 안에는 원문에서 찾을 수 있는 '핵심 스타일 키워드', '브랜드', '장소', '무드'가 자연스럽게 포함되어야 합니다.
                3.  번역: 생성한 한국어 제목과 요약문을, 원문의 뉘앙스를 살려 자연스러운 "영어"로 번역합니다.
                4.  출력 형식: 다른 말은 절대 하지 말고, 반드시 아래와 같은 JSON 형식으로만 응답해 주세요.
                
                # 출력 JSON 형식:
                {
                  "titleKo": "새로 생성한 한국어 제목",
                  "summaryKo": "새로 생성한 한국어 요약문 (3-4 문장).",
                  "titleEn": "Translated English Title",
                  "summaryEn": "Translated English Summary (3-4 sentences)."
                }
                
                # 원문 기사 제목:
                [ [ [ %s ] ] ]
                
                # 원문 기사 본문:
                [ [ [ %s ] ] ]
                """;

        String formattedPrompt = String.format(
                prompt,
                originalTitle,
                originalBody.substring(0, Math.min(originalBody.length(), 4000))
        );
        String requestBodyJson = createRequestBody(formattedPrompt);

        try {
            String url = GEMINI_API_URL + geminiProperties.getKey();
            String responseJson = restTemplate.postForObject(url, requestBodyJson, String.class);

            return parseTranslationResponse(responseJson);
        } catch (Exception e) {
            log.error("Gemini API 호출 실패 : {}", e.getMessage());
            return null;
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

    private AiTranslationResponse parseTranslationResponse(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);

        // 1. Gemini의 첫 번째 응답 텍스트를 추출
        JsonNode textNode = root
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text");

        if (textNode.isTextual()) {
            String innerJsonText = textNode.asText().trim();

            // 2. (선택적이지만 강력히 추천) Gemini가 JSON을 ```json ... ```으로 감싸는 경우가 있음
            if (innerJsonText.startsWith("```json")) {
                innerJsonText = innerJsonText.substring(7, innerJsonText.length() - 3).trim();
            }

            // 3. [핵심] 추출한 텍스트(JSON)를 AiTranslationResponse DTO로 파싱
            try {
                return objectMapper.readValue(innerJsonText, AiTranslationResponse.class);
            } catch (Exception e) {
                log.error("Gemini 내부 JSON 응답 파싱 실패: {}", innerJsonText, e);
                throw new Exception("AI가 유효하지 않은 JSON 형식을 반환했습니다.");
            }
        }

        log.warn("Gemini 응답 구조 오류: {}", responseJson);
        throw new Exception("AI 요약 실패: 유효하지 않은 응답 구조");
    }

}
