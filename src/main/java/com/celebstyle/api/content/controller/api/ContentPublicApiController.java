package com.celebstyle.api.content.controller.api;

import com.celebstyle.api.content.service.ContentPublicService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentPublicApiController {

    private final ContentPublicService publicService;

    @PostMapping("/{contentId}/view")
    public ResponseEntity<Void> viewCounting(
            @PathVariable Long contentId,
            HttpServletRequest request,
            HttpServletResponse response) {

        String cookieName = "viewed_contents";
        String cookieValue = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }
        String targetId = "|" + contentId + "|";
        if (!cookieValue.contains(targetId)) {

            publicService.plusViewCount(contentId);

            cookieValue += targetId;
            Cookie newCookie = new Cookie(cookieName, cookieValue);
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); // 24시간
            response.addCookie(newCookie);
        }

        return ResponseEntity.ok().build();

    }
}
