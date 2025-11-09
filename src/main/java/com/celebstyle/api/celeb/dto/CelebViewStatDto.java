package com.celebstyle.api.celeb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CelebViewStatDto {
    private Long celebId;
    private String nameKo;
    private String nameEn;
    private Long totalViewCount;
}
