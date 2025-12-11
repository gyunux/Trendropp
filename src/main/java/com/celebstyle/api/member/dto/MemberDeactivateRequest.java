package com.celebstyle.api.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDeactivateRequest {
    @NotBlank
    private String currentPassword;
}
