package com.celebstyle.api.brand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BrandCreateRequest {
    @NotBlank
    private String englishName;

    @NotBlank
    private String koreanName;
}
