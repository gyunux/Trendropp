package com.celebstyle.api.outfit.dto;

import com.celebstyle.api.celeb.dto.CelebForOutfitDetail;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.outfit.Outfit;
import com.celebstyle.api.outfit.SourceType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class OutfitDetailView {

    private Long id;
    private String title;
    private String originImageUrl;
    private SourceType sourceType;
    private LocalDateTime sourceDate;

    private CelebForOutfitDetail celeb;
    private List<ItemDetailView> items;
    private String summary;

    private OutfitDetailView(
            Long id,
            String title,
            String originImageUrl,
            SourceType sourceType,
            LocalDateTime sourceDate,
            CelebForOutfitDetail celeb,
            List<ItemDetailView> items,
            String summary
    ) {
        this.id = id;
        this.title = title;
        this.originImageUrl = originImageUrl;
        this.sourceType = sourceType;
        this.sourceDate = sourceDate;
        this.celeb = celeb;
        this.items = items;
        this.summary = summary;
    }

    public static OutfitDetailView fromEntity(Outfit outfit) {
        List<ItemDetailView> itemDtos = outfit.getOutfitItems().stream()
                .map(outfitItem -> ItemDetailView.fromEntity(outfitItem.getItem()))
                .collect(Collectors.toList());

        return new OutfitDetailView(
                outfit.getId(),
                outfit.getTitle(),
                outfit.getOriginImageUrl(),
                outfit.getSourceType(),
                outfit.getSourceDate(),
                new CelebForOutfitDetail(outfit.getCeleb()),
                itemDtos,
                outfit.getSummary()
        );
    }
}