package com.celebstyle.api.content.dto;

import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.SourceType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ContentAdminView {
    private Long id;
    private String title;
    private String originImageUrl;
    private String sourceUrl;
    private LocalDateTime sourceDate;
    private LocalDateTime uploadDate;
    private SourceType sourceType;
    private CelebView celeb;
    private int itemCount;

    public static ContentAdminView fromEntity(Content content) {
        return new ContentAdminView(
                content.getId(),
                content.getTitle(),
                content.getOriginImageUrl(),
                content.getSourceUrl(),
                content.getSourceDate(),
                content.getUploadDate(),
                content.getSourceType(),
                new CelebView(content.getCeleb()),
                content.getContentItems().size()
        );
    }

    private ContentAdminView(
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
