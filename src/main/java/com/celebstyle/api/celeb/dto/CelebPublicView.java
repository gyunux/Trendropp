package com.celebstyle.api.celeb.dto;

import com.celebstyle.api.celeb.Celeb;
import lombok.Getter;

@Getter
public class CelebPublicView {
    private String name;

    public CelebPublicView(Celeb celeb) {
        this.name = celeb.getName();
    }
}