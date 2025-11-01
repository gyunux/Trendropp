package com.celebstyle.api.member.service;

import com.celebstyle.api.member.Member;
import com.celebstyle.api.member.MemberRepository;
import com.celebstyle.api.member.Role;
import com.celebstyle.api.member.dto.EmailChangeRequest;
import com.celebstyle.api.member.dto.MemberMyPageView;
import com.celebstyle.api.member.dto.MemberSignupRequest;
import com.celebstyle.api.member.dto.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Transactional(readOnly = true)
    public MemberMyPageView getMemberMyPage(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 찾을 수 없습니다."));

        return new MemberMyPageView(member);
    }

    @Transactional
    public void changeEmail(Long memberId, EmailChangeRequest request) {
        try {
            Member member = memberRepository.findById(memberId).orElseThrow();

            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }

            if (memberRepository.existsByEmail(request.getNewEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }

            member.updateEmail(request.getNewEmail());

        } catch (IllegalArgumentException e) {
            log.error("이메일 변경 중 오류: {}", e.getMessage());
        }
    }

    @Transactional
    public void changePassword(Long memberId, PasswordChangeRequest request) {
        try {
            Member member = memberRepository.findById(memberId).orElseThrow();

            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            member.updatePassword(encodedPassword);
        } catch (IllegalArgumentException e) {
            log.error("비밀번호 변경 중 오류: {}", e.getMessage());
        }
    }

    @Transactional
    public void deactivateMember(Long memberId, String currentPassword) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        member.deactivate();
    }

}
