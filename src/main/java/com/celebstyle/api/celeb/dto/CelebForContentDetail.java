package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import java.util.Locale;
import lombok.Getter;

@Getter
public class CelebForContentDetail {
    private Long id;
    private String profileImageUrl;
    private String name;

    public CelebForContentDetail(Celeb celeb, Locale locale) {
        this.id = celeb.getId();
        this.profileImageUrl = celeb.getProfileImageUrl();

        boolean isEnglish = locale.getLanguage().equals("en");
        this.name = isEnglish ? celeb.getNameEn() : celeb.getNameKo();
    }
}
