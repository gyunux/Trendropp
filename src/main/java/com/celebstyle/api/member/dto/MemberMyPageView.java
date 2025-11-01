package com.celebstyle.api.member.dto;

import com.celebstyle.api.member.Member;
import lombok.Getter;

@Getter
public class MemberMyPageView {
    private final String userId;
    private final String name;
    private final String email;

    public MemberMyPageView(Member member) {
        this.userId = member.getUserId();
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
