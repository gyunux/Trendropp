package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import java.util.Locale;
import lombok.Getter;

@Getter
public class CelebPublicView {
    private String name;

    public CelebPublicView(Celeb celeb, Locale locale) {
        boolean isEnglish = locale.getLanguage().equals("en");
        this.name = isEnglish ? celeb.getNameEn() : celeb.getNameKo();
    }
}