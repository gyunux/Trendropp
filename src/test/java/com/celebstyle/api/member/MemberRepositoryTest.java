package com.celebstyle.api.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.celebstyle.api.article.repository.ArticleRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @MockitoBean
    ArticleRepositoryImpl articleRepository;

    private Member savedMember;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();

        savedMember = memberRepository.save(member);
    }

    @Test
    @DisplayName("회원 조회 테스트")
    void memberFindTest() {

        Member findMember = memberRepository.findByUserIdAndDeletedAtIsNull("test").orElse(null);

        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo("test@celebstyle.com");
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("회원 이메일 및 패스워드 수정 테스트")
    void memberUpdateTest() {
        String newPassword = "updated_password";
        String newEmail = "updated@celebstyle.com";

        savedMember.updatePassword(newPassword);
        savedMember.updateEmail(newEmail);

        entityManager.flush();
        entityManager.clear();

        Member updatedMember = memberRepository.findById(savedMember.getId()).orElseThrow();

        assertThat(updatedMember.getPassword()).isEqualTo(newPassword);
        assertThat(updatedMember.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("회원 삭제 테스트(Soft Delete)")
    void memberDeactivate() {
        savedMember.deactivate();

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUserId()).startsWith("DELETED_");
        assertThat(savedMember.getEmail()).startsWith("DELETED_");
        assertThat(savedMember.getName()).isEqualTo("탈퇴한 회원");

    }
}
