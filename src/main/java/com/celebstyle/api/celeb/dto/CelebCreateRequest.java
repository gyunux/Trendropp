package com.celebstyle.api.celeb.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CelebCreateRequest {
    @NotBlank
    @Size(min = 2,max = 50)
    private String name;

    @NotBlank
    @Size(max = 2048)
    private String profileImageUrl;

    @NotBlank
    @Size(max = 2048)
    private String instagramName;
}