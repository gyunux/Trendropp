package com.celebstyle.api.brand.dto;

import com.celebstyle.api.brand.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BrandView {
    private Long id;
    private String englishName;

    public BrandView(Brand brand){
        this.id = brand.getId();
        this.englishName = brand.getEnglishName();
    }
}
