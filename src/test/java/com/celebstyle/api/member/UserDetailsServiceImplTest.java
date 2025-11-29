package com.celebstyle.api.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.celebstyle.api.member.service.UserDetailsServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 성공 - 정상 회원 조회")
    void loadUserByUsername_Success() {
        String userId = "testUser";
        Member member = Member.builder()
                .userId(userId)
                .password("encodedPw")
                .role(Role.USER)
                .build();

        given(memberRepository.findByUserIdAndDeletedAtIsNull(userId))
                .willReturn(Optional.of(member));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(userId);
        assertThat(userDetails.getPassword()).isEqualTo("encodedPw");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않거나 탈퇴한 회원")
    void loadUserByUsername_Fail_NotFound() {
        String userId = "unknownUser";

        given(memberRepository.findByUserIdAndDeletedAtIsNull(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("아이디를 찾을 수 없거나 탈퇴한 회원입니다");
    }
}
