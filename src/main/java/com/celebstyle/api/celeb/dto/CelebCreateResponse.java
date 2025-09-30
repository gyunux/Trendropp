package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CelebCreateResponse {
    private Long id;
    private String name;
    private String profileImageUrl;
    private String instagramName;

    @Builder
    private CelebCreateResponse(Long id,String name,String profileImageUrl,String instagramName) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.instagramName = instagramName;
    }

    public static CelebCreateResponse fromEntity(Celeb celeb){
        return CelebCreateResponse.builder()
                .id(celeb.getId())
                .name(celeb.getName())
                .profileImageUrl(celeb.getProfileImageUrl())
                .instagramName(celeb.getInstagramName())
                .build();
    }
}
