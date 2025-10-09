package com.celebstyle.api.item.dto;

import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.item.Item;
import lombok.Getter;

@Getter
public class ItemDetailView {
    private Long id;
    private BrandView brand;
    private String name;
    private String imageUrl;
    private String productUrl;

    public static ItemDetailView fromEntity(Item item){
        return new ItemDetailView(
                item.getId(),
                BrandView.fromEntity(item.getBrand()),
                item.getName(),
                item.getImageUrl(),
                item.getProductUrl());
    }

    private ItemDetailView(
            Long id,
            BrandView brand,
            String name,
            String imageUrl,
            String productUrl
    ){
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
    }
}
