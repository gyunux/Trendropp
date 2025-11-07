package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import lombok.Getter;

@Getter
public class CelebView {
    private Long id;
    private String name;

    public CelebView(Celeb celeb) {
        this.id = celeb.getId();
        this.name = celeb.getNameKo();
    }
}