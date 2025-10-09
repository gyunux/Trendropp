package com.celebstyle.api.brand.dto;

import com.celebstyle.api.brand.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrandView {
    private Long id;
    private String englishName;
    private String koreanName;

    public static BrandView fromEntity(Brand brand) {
        return new BrandView(brand.getId(), brand.getEnglishName(), brand.getKoreanName());
    }
}
