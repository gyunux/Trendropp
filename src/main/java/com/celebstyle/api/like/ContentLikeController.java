package com.celebstyle.api.like;

import com.celebstyle.api.content.dto.ContentPublicView;
import com.celebstyle.api.member.CustomUserDetails;
import java.util.List;
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
    public String getLikes(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getMember().getId();
        List<ContentPublicView> contentPublicViews = contentLikeService.getMyLikes(userId);

        model.addAttribute("contents", contentPublicViews);
        return "likes";
    }
}
