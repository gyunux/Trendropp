package com.celebstyle.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailChangeRequest {
    @NotBlank(message = "새 이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String newEmail;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
}
