package com.celebstyle.api.member.controller;

import com.celebstyle.api.member.dto.MemberSignupRequest;
import com.celebstyle.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("memberSignupRequest", new MemberSignupRequest());
        return "signup";
    }
}
