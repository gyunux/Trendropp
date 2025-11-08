package com.celebstyle.api.magazine.service;

import com.celebstyle.api.magazine.dto.AiTranslationResponse;

public interface AiService {
    AiTranslationResponse getSummariesAndTranslations(String title, String body);
}
