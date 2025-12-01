package com.celebstyle.api.item.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemRequest {
    private Long brandId;
    private String itemName;
    private MultipartFile itemImageFile;
    private String productUrl;

    // [추가] 수정 시 이미지를 안 바꿨으면 이 URL을 씀
    private String originalImageUrl;
}
