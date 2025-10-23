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
}
