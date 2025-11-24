package com.celebstyle.api.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class CustomUserDetailsTest {

    @Test
    @DisplayName("권한 목록(Authorities) 변환 테스트")
    void getAuthorities_Success() {
        Member member = Member.builder()
                .userId("testUser")
                .password("password")
                .role(Role.USER)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(member);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).hasSize(1);

        GrantedAuthority authority = authorities.iterator().next();
        assertThat(authority.getAuthority()).isEqualTo(member.getRole().getKey());
    }

    @Test
    @DisplayName("회원 정보 위임(Getter) 테스트")
    void userDetails_Getter_Delegation_Success() {
        Member member = Member.builder()
                .userId("testUser")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(member);

        assertThat(userDetails.getUsername()).isEqualTo("testUser");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");

        assertThat(userDetails.getMember()).isEqualTo(member);
    }
}