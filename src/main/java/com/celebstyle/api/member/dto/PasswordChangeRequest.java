package com.celebstyle.api.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$",
            message = "비밀번호는 6자 이상이어야 하며, 소문자와 숫자를 반드시 포함해야 합니다.")
    private String newPassword;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
}
