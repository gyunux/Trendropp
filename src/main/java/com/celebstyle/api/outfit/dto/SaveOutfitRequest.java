package com.celebstyle.api.outfit.dto;

import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.outfit.SourceType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class SaveOutfitRequest {
    private String title;
    private Long celebId;
    private Long sourceArticleId;
    private SourceType sourceType;
    private String sourceUrl;
    private LocalDateTime sourceDate;
    private String mainImageUrl;
    private List<ItemRequest> items;
}
