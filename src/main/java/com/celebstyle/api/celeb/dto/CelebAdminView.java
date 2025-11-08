package com.celebstyle.api.celeb.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CelebAdminView {
    private Long id;
    private String profileImageUrl;
    private String nameKo;
    private String nameEn;
    private String instagramName;

    @Builder
    public CelebAdminView(Long id, String profileImageUrl, String nameKo, String nameEn, String instagramName) {
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.instagramName = instagramName;
    }
}
