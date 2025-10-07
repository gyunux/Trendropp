package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import lombok.Getter;

@Getter
public class CelebForOutfitDetail {
    private Long id;
    private String profileImageUrl;
    private String name;

    public CelebForOutfitDetail(Celeb celeb) {
        this.id = celeb.getId();
        this.name = celeb.getName();
        this.profileImageUrl = celeb.getProfileImageUrl();
    }
}
