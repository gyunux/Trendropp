package com.celebstyle.api.content.dto;

import com.celebstyle.api.celeb.dto.CelebForContentDetail;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.SourceType;
import com.celebstyle.api.item.dto.ItemDetailView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
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
    private boolean isLiked;
    private Long viewCount;
    private String sourceUrl;

    private ContentDetailView(
            Long id,
            String title,
            String originImageUrl,
            SourceType sourceType,
            LocalDateTime sourceDate,
            CelebForContentDetail celeb,
            List<ItemDetailView> items,
            String summary,
            boolean isLiked,
            Long viewCount,
            String sourceUrl
    ) {
        this.id = id;
        this.title = title;
        this.originImageUrl = originImageUrl;
        this.sourceType = sourceType;
        this.sourceDate = sourceDate;
        this.celeb = celeb;
        this.items = items;
        this.summary = summary;
        this.isLiked = isLiked;
        this.viewCount = viewCount;
        this.sourceUrl = sourceUrl;
    }

    public static ContentDetailView fromEntity(Content content, boolean isLiked, Locale locale) {
        boolean isEnglish = locale.getLanguage().equals("en");

        List<ItemDetailView> itemDtos = content.getContentItems().stream()
                .map(contentItem -> ItemDetailView.fromEntity(contentItem.getItem()))
                .collect(Collectors.toList());

        CelebForContentDetail celebDto = new CelebForContentDetail(content.getCeleb(), locale);

        return new ContentDetailView(
                content.getId(),
                isEnglish ? content.getTitleEn() : content.getTitleKo(),
                content.getOriginImageUrl(),
                content.getSourceType(),
                content.getSourceDate(),
                celebDto,
                itemDtos,
                isEnglish ? content.getSummaryEn() : content.getSummaryKo(),
                isLiked,
                content.getViewCount(),
                content.getSourceUrl()
        );
    }
}