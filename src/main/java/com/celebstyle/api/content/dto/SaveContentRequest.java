package com.celebstyle.api.content.dto;

import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.content.SourceType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SaveContentRequest {
    private String title;
    private Long celebId;
    private Long sourceArticleId;
    private SourceType sourceType;
    private String sourceUrl;
    private LocalDateTime sourceDate;
    private String mainImageUrl;
    private List<ItemRequest> items;
    private String summary;
}
