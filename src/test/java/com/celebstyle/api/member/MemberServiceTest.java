package com.celebstyle.api.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.celebstyle.api.member.dto.EmailChangeRequest;
import com.celebstyle.api.member.dto.MemberMyPageView;
import com.celebstyle.api.member.dto.MemberSignupRequest;
import com.celebstyle.api.member.dto.PasswordChangeRequest;
import com.celebstyle.api.member.service.MemberService;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 생성 서비스 로직 테스트")
    void memberSignUpTest() {
        MemberSignupRequest request = new MemberSignupRequest();
        request.setUserId("test");
        request.setName("테스트 유저");
        request.setEmail("test@test.com");
        request.setPassword("password");

        given(passwordEncoder.encode("password")).willReturn("encodedPassword");

        memberService.signUp(request);

        verify(memberRepository).save(argThat(member ->
                member.getUserId().equals("test") &&
                        member.getName().equals("테스트 유저") &&
                        member.getEmail().equals("test@test.com") &&
                        member.getPassword().equals("encodedPassword") // 암호화된 비번이 들어갔는지 핵심 체크!
        ));
    }

    @Test
    @DisplayName("아이디 중복 확인 - 이미 존재하는 경우 true 반환")
    void isUsernameDuplicated_True() {
        String userId = "duplicatedUser";
        given(memberRepository.existsByUserId(userId)).willReturn(true);

        boolean result = memberService.isUsernameDuplicated(userId);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("아이디 중복 확인 - 존재하지 않는 경우 false 반환")
    void isUsernameDuplicated_False() {
        String userId = "newUser";
        given(memberRepository.existsByUserId(userId)).willReturn(false);

        boolean result = memberService.isUsernameDuplicated(userId);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 이미 존재하는 경우 true 반환")
    void isEmailDuplicated_True() {
        String email = "exist@celebstyle.com";
        given(memberRepository.existsByEmail(email)).willReturn(true);

        boolean result = memberService.isEmailDuplicated(email);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하지 않는 경우 false 반환")
    void isEmailDuplicated_False() {
        String email = "newUser@celebstyle.com";
        given(memberRepository.existsByEmail(email)).willReturn(false);

        boolean result = memberService.isEmailDuplicated(email);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원 마이 페이지 조회 테스트")
    void getMemberMypageTest() {
        Member member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();

        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));

        MemberMyPageView memberMyPageView = memberService.getMemberMyPage(1L);

        assertThat(memberMyPageView.getUserId()).isEqualTo("test");
        assertThat(memberMyPageView.getEmail()).isEqualTo("test@celebstyle.com");
        assertThat(memberMyPageView.getName()).isEqualTo("테스트 멤버");
    }

    @Test
    @DisplayName("회원 이메일 수정 테스트 성공 - 모든 조건 통과")
    void memberEmailChangeSuccessTest() {
        Long memberId = 1L;
        EmailChangeRequest request = new EmailChangeRequest();

        request.setCurrentPassword("oldPass");
        request.setNewEmail("new@celebstyle.com");

        Member member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches("oldPass", "test_password")).willReturn(true);
        given(memberRepository.existsByEmail("new@celebstyle.com")).willReturn(false);

        memberService.changeEmail(memberId, request);

        assertThat(Objects.requireNonNull(member).getEmail()).isEqualTo("new@celebstyle.com");
    }

    @Test
    @DisplayName("회원 이메일 수정 테스트 실패 - 기존 비밀번호 불일치")
    void memberEmailChangeFailureTest1() {
        Long memberId = 1L;
        EmailChangeRequest request = new EmailChangeRequest();

        request.setCurrentPassword("oldPass");
        request.setNewEmail("new@celebstyle.com");

        Member member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches("oldPass", "test_password")).willReturn(false);

        assertThatThrownBy(() -> memberService.changeEmail(memberId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("회원 이메일 수정 테스트 실패 - 비밀번호 일치 이메일 중복")
    void memberEmailChangeFailureTest2() {
        Long memberId = 1L;
        EmailChangeRequest request = new EmailChangeRequest();

        request.setCurrentPassword("oldPass");
        request.setNewEmail("new@celebstyle.com");

        Member member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(member));
        given(passwordEncoder.matches("oldPass", "test_password")).willReturn(true);
        given(memberRepository.existsByEmail("new@celebstyle.com")).willReturn(true);

        assertThatThrownBy(() -> memberService.changeEmail(memberId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("비밀번호 변경 성공 - 정상 흐름")
    void changePassword_Success() {
        Long memberId = 1L;
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setCurrentPassword("oldPass");
        req.setNewPassword("newPass");

        Member member = Member.builder()
                .password("encodedOldPass")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("oldPass", "encodedOldPass")).willReturn(true);
        given(passwordEncoder.encode("newPass")).willReturn("encodedNewPass");

        memberService.changePassword(memberId, req);

        assertThat(member.getPassword()).isEqualTo("encodedNewPass");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void changePassword_Fail_WrongPassword() {
        Long memberId = 1L;
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setCurrentPassword("wrongPass");
        req.setNewPassword("newPass");
        Member member = Member.builder().password("encodedOldPass").build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("wrongPass", "encodedOldPass")).willReturn(false);

        assertThatThrownBy(() -> memberService.changePassword(memberId, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");

        assertThat(member.getPassword()).isEqualTo("encodedOldPass");
    }

    @Test
    @DisplayName("회원 탈퇴 성공 - 정상 흐름")
    void deactivateMember_Success() {
        Long memberId = 1L;
        String currentPassword = "validPass";

        Member member = Member.builder()
                .password("encodedPass")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(currentPassword, "encodedPass")).willReturn(true);

        memberService.deactivateMember(memberId, currentPassword);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 현재 비밀번호 불일치")
    void deactivateMember_Fail_WrongPassword() {
        Long memberId = 1L;
        String currentPassword = "wrongPass";
        Member member = Member.builder().password("encodedPass").build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(currentPassword, "encodedPass")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.deactivateMember(memberId, currentPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");

    }

}
