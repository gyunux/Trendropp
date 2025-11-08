package com.celebstyle.api.like;

import com.celebstyle.api.content.dto.ContentPublicView;
import com.celebstyle.api.member.CustomUserDetails;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/likes")
public class ContentLikeController {
    private final ContentLikeService contentLikeService;

    @GetMapping
    public String getLikes(@AuthenticationPrincipal CustomUserDetails userDetails, Model model, Locale locale) {
        Long userId = userDetails.getMember().getId();
        List<ContentPublicView> contentPublicViews = contentLikeService.getMyLikes(userId, locale);

        model.addAttribute("contents", contentPublicViews);
        return "likes";
    }
}
