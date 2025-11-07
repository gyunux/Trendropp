package com.celebstyle.api.content.controller.web;

import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.service.ContentPublicService;
import com.celebstyle.api.member.CustomUserDetails;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentPublicController {
    private final ContentPublicService contentPublicService;

    @GetMapping("/{id}")
    public String getContent(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model, Locale locale) {

        Long currentMemberId = null;
        if (userDetails != null) {
            currentMemberId = userDetails.getMember().getId(); // [핵심 2]
        }
        ContentDetailView contentDetailView = contentPublicService.getContentDetail(id, currentMemberId, locale);
        model.addAttribute("content", contentDetailView);
        return "content-detail";
    }
}
