package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import lombok.Getter;

@Getter
public class CelebForContentDetail {
    private Long id;
    private String profileImageUrl;
    private String name;

    public CelebForContentDetail(Celeb celeb) {
        this.id = celeb.getId();
        this.name = celeb.getName();
        this.profileImageUrl = celeb.getProfileImageUrl();
    }
}
