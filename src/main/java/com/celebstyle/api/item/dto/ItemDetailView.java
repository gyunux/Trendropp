package com.celebstyle.api.item.dto;

import com.celebstyle.api.item.Item;
import lombok.Getter;

@Getter
public class ItemDetailView {
    private Long id;
    private String brandName;
    private String productName;
    private String productImageUrl;
    private String productUrl;

    public static ItemDetailView fromEntity(Item item){
        return new ItemDetailView(
                item.getId(),
                item.getBrand().getEnglishName(),
                item.getName(),
                item.getImageUrl(),
                item.getProductUrl());
    }

    private ItemDetailView(
            Long id,
            String brandName,
            String productName,
            String productImageUrl,
            String productUrl
    ){
        this.id = id;
        this.brandName = brandName;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productUrl = productUrl;
    }
}
