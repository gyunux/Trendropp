package com.celebstyle.api.like;

import com.celebstyle.api.member.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentLikeApiController {
    private final ContentLikeService contentLikeService;

    @PostMapping("/{contentId}/like")
    public ResponseEntity<Void> addLike(@PathVariable Long contentId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMember().getId();

        contentLikeService.addLike(memberId, contentId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{contentId}/like")
    public ResponseEntity<Void> removeLike(@PathVariable Long contentId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMember().getId();

        contentLikeService.removeLike(memberId, contentId);

        return ResponseEntity.ok().build();
    }
}
