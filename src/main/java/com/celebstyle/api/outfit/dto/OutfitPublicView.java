package com.celebstyle.api.outfit.dto;

import com.celebstyle.api.celeb.dto.CelebPublicView;
import com.celebstyle.api.outfit.Outfit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OutfitPublicView {
    private Long id;
    private String title;
    private String originImageUrl;
    private CelebPublicView celeb;
    private int itemCount;

    public static OutfitPublicView fromEntity(Outfit outfit) {
        return new OutfitPublicView(
                outfit.getId(),
                outfit.getTitle(),
                outfit.getOriginImageUrl(),
                new CelebPublicView(outfit.getCeleb()),
                outfit.getOutfitItems().size()
        );
    }
}