package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CelebCreateResponse {
    private Long id;
    private String nameKo;
    private String nameEn;
    private String profileImageUrl;
    private String instagramName;

    @Builder
    private CelebCreateResponse(Long id, String nameKo, String nameEn, String profileImageUrl, String instagramName) {
        this.id = id;
        this.nameKo = nameKo;
        this.profileImageUrl = profileImageUrl;
        this.instagramName = instagramName;
    }

    public static CelebCreateResponse fromEntity(Celeb celeb) {
        return CelebCreateResponse.builder()
                .id(celeb.getId())
                .nameKo(celeb.getNameKo())
                .nameEn(celeb.getNameEn())
                .profileImageUrl(celeb.getProfileImageUrl())
                .instagramName(celeb.getInstagramName())
                .build();
    }
}
