package com.celebstyle.api.outfit.dto;

import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.outfit.Outfit;
import com.celebstyle.api.outfit.SourceType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class OutfitAdminView {
    private Long id;
    private String title;
    private String originImageUrl;
    private String sourceUrl;
    private LocalDateTime sourceDate;
    private LocalDateTime uploadDate;
    private SourceType sourceType;
    private CelebView celeb;
    private int itemCount;

    public static OutfitAdminView fromEntity(Outfit outfit) {
        return new OutfitAdminView(
                outfit.getId(),
                outfit.getTitle(),
                outfit.getOriginImageUrl(),
                outfit.getSourceUrl(),
                outfit.getSourceDate(),
                outfit.getUploadDate(),
                outfit.getSourceType(),
                new CelebView(outfit.getCeleb()),
                outfit.getOutfitItems().size()
        );
    }

    private OutfitAdminView(
            Long id,
            String title,
            String originImageUrl,
            String sourceUrl,
            LocalDateTime sourceDate,
            LocalDateTime uploadDate,
            SourceType sourceType,
            CelebView celeb,
            int itemCount
    ) {
        this.id = id;
        this.title = title;
        this.originImageUrl = originImageUrl;
        this.sourceUrl = sourceUrl;
        this.sourceDate = sourceDate;
        this.uploadDate = uploadDate;
        this.sourceType = sourceType;
        this.celeb = celeb;
        this.itemCount = itemCount;
    }
}
