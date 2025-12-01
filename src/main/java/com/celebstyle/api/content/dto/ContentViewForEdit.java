package com.celebstyle.api.content.dto;

import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.SourceType;
import com.celebstyle.api.item.dto.ItemDetailView;
import java.util.List;
import lombok.Getter;

@Getter
public class ContentViewForEdit {
    private Long id;
    private String titleKo;
    private String titleEn;
    private String summaryKo;
    private String summaryEn;
    private String sourceUrl;
    private SourceType sourceType;
    private CelebView celeb;
    private String contentImage;
    private List<ItemDetailView> items;

    public static ContentViewForEdit fromEntity(Content content, CelebView celebView, List<ItemDetailView> items) {
        return new ContentViewForEdit(
                content.getId(),
                content.getTitleKo(),
                content.getTitleEn(),
                content.getSummaryKo(),
                content.getSummaryEn(),
                content.getSourceUrl(),
                content.getSourceType(),
                celebView,
                content.getOriginImageUrl(),
                items
        );
    }

    private ContentViewForEdit(
            Long id,
            String titleKo,
            String titleEn,
            String summaryKo,   // [추가]
            String summaryEn,
            String sourceUrl,
            SourceType sourceType,
            CelebView celeb,
            String contentImage,
            List<ItemDetailView> items
    ) {
        this.id = id;
        this.titleKo = titleKo;
        this.titleEn = titleEn;
        this.summaryKo = summaryKo;
        this.summaryEn = summaryEn;
        this.sourceUrl = sourceUrl;
        this.sourceType = sourceType;
        this.celeb = celeb;
        this.contentImage = contentImage;
        this.items = items;
    }
}
