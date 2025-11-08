package com.celebstyle.api.common;

import com.celebstyle.api.content.dto.ContentPublicView;
import com.celebstyle.api.content.service.ContentPublicService;
import com.celebstyle.api.member.CustomUserDetails;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainPageController {

    private final ContentPublicService contentService;

    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, Locale locale) {
        Long currentMemberId = null;
        if (userDetails != null) {
            currentMemberId = userDetails.getMember().getId();
        }

        List<ContentPublicView> contents = contentService.findAllForMainPage(currentMemberId, locale);

        model.addAttribute("contents", contents);
        return "index";
    }
}