package com.celebstyle.api.item.dto;

import lombok.Getter;

@Getter
public class ItemRequest {
    private Long brandId;
    private String itemName;
    private String itemImageUrl;
    private String productUrl;
}
