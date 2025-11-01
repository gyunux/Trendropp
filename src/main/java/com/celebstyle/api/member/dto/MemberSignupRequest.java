package com.celebstyle.api.member.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Valid
public class MemberSignupRequest {

    @Size(min = 1, max = 20, message = "유저 네임은 1~20 글자여야 합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$",
            message = "비밀번호는 6자 이상이어야 하며, 소문자와 숫자를 반드시 포함해야 합니다.")
    private String password;

    @NotBlank
    @Length(max = 20)
    private String name;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
}
