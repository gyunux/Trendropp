package com.celebstyle.api.celeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CelebUpdateRequest {

    @NotBlank
    private String profileImageUrl;

    @NotBlank
    @Size(max = 2048)
    private String instagramName;
}
