package com.celebstyle.api.member.controller;

import com.celebstyle.api.member.CustomUserDetails;
import com.celebstyle.api.member.dto.MemberMyPageView;
import com.celebstyle.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final MemberService memberService;

    @GetMapping
    public String getMember(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getMember().getId();
        MemberMyPageView memberMyPageView = memberService.getMemberMyPage(userId);
        model.addAttribute("member", memberMyPageView);
        return "mypage";
    }
}
