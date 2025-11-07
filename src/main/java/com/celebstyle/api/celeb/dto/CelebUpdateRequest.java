package com.celebstyle.api.celeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CelebUpdateRequest {
    
    @NotBlank
    private String nameKo;

    @NotBlank
    private String nameEn;

    private MultipartFile profileImage;

    @NotBlank
    @Size(max = 2048)
    private String instagramName;
}
