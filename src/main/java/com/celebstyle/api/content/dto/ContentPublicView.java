package com.celebstyle.api.content.dto;

import com.celebstyle.api.celeb.dto.CelebPublicView;
import com.celebstyle.api.content.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentPublicView {
    private Long id;
    private String title;
    private String originImageUrl;
    private CelebPublicView celeb;
    private int itemCount;

    public static ContentPublicView fromEntity(Content content) {
        return new ContentPublicView(
                content.getId(),
                content.getTitle(),
                content.getOriginImageUrl(),
                new CelebPublicView(content.getCeleb()),
                content.getContentItems().size()
        );
    }
}