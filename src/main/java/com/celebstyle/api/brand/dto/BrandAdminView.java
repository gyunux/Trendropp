package com.celebstyle.api.brand.dto;

import com.celebstyle.api.brand.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrandAdminView {
    private Long id;
    private String englishName;
    private String koreanName;

    public static BrandAdminView fromEntity(Brand brand) {
        return new BrandAdminView(brand.getId(), brand.getEnglishName(), brand.getKoreanName());
    }
}
