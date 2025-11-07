package com.celebstyle.api.celeb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CelebCreateRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String nameKo;

    @NotBlank
    @Size(min = 2, max = 50)
    private String nameEn;

    @NotBlank
    @Size(max = 2048)
    private String instagramName;

    //@ModelAttribute 사용
    private MultipartFile profileImage;
}