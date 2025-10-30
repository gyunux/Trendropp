package com.celebstyle.api.member.service;

import com.celebstyle.api.member.Member;
import com.celebstyle.api.member.MemberRepository;
import com.celebstyle.api.member.Role;
import com.celebstyle.api.member.dto.MemberSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(MemberSignupRequest req) {
        //비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        Member member = Member.builder()
                .userId(req.getUserId())
                .password(encodedPassword)
                .name(req.getName())
                .email(req.getEmail())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public boolean isUsernameDuplicated(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }
}
