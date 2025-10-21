package com.celebstyle.api.content.dto;

import com.celebstyle.api.celeb.dto.CelebForContentDetail;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.content.SourceType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ContentDetailView {

    private Long id;
    private String title;
    private String originImageUrl;
    private SourceType sourceType;
    private LocalDateTime sourceDate;

    private CelebForContentDetail celeb;
    private List<ItemDetailView> items;
    private String summary;

    private ContentDetailView(
            Long id,
            String title,
            String originImageUrl,
            SourceType sourceType,
            LocalDateTime sourceDate,
            CelebForContentDetail celeb,
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

    public static ContentDetailView fromEntity(Content content) {
        List<ItemDetailView> itemDtos = content.getContentItems().stream()
                .map(contentItem -> ItemDetailView.fromEntity(contentItem.getItem()))
                .collect(Collectors.toList());

        return new ContentDetailView(
                content.getId(),
                content.getTitle(),
                content.getOriginImageUrl(),
                content.getSourceType(),
                content.getSourceDate(),
                new CelebForContentDetail(content.getCeleb()),
                itemDtos,
                content.getSummary()
        );
    }
}