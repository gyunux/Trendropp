package com.celebstyle.api.magazine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AiTranslationResponse {
    private String titleKo;
    private String summaryKo;
    private String titleEn;
    private String summaryEn;
}
