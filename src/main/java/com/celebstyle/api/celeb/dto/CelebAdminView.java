package com.celebstyle.api.celeb.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CelebAdminView {
    private Long id;
    private String profileImageUrl;
    private String name;
    private String instagramName;

    @Builder
    public CelebAdminView(Long id, String profileImageUrl, String name, String instagramName){
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.instagramName = instagramName;
    }
}
