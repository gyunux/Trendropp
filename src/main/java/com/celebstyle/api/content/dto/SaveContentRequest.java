package com.celebstyle.api.content.dto;

import com.celebstyle.api.content.SourceType;
import com.celebstyle.api.item.dto.ItemRequest;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveContentRequest {
    @NotBlank(message = "한국어 제목은 필수입니다.")
    private String titleKo;

    @NotBlank(message = "영어 제목은 필수입니다.")
    private String titleEn;

    private String summaryKo;

    private String summaryEn;

    private Long celebId;
    private Long sourceArticleId;
    private SourceType sourceType;
    private String sourceUrl;
    private LocalDateTime sourceDate;
    private String mainImageUrl;
    private List<ItemRequest> items;
}
