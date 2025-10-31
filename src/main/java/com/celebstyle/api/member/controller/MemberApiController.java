package com.celebstyle.api.member.controller;

import com.celebstyle.api.member.CustomUserDetails;
import com.celebstyle.api.member.dto.EmailChangeRequest;
import com.celebstyle.api.member.dto.MemberSignupRequest;
import com.celebstyle.api.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody MemberSignupRequest request) {
        memberService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/check-userid")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String userId) {
        boolean isAvailable = !memberService.isUsernameDuplicated(userId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isAvailable = !memberService.isEmailDuplicated(email);
        return ResponseEntity.ok(isAvailable);
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> changeEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EmailChangeRequest request) {

        Long currentMemberId = userDetails.getMember().getId();
        memberService.changeEmail(currentMemberId, request);

        return ResponseEntity.ok().build();
    }
}
