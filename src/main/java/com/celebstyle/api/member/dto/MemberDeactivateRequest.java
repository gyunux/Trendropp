package com.celebstyle.api.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDeactivateRequest {
    private String currentPassword;
}
