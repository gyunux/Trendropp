package com.celebstyle.api.like;

import static org.assertj.core.api.Assertions.assertThat;

import com.celebstyle.api.article.repository.ArticleRepositoryImpl;
import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.ContentRepository;
import com.celebstyle.api.content.SourceType;
import com.celebstyle.api.member.Member;
import com.celebstyle.api.member.MemberRepository;
import com.celebstyle.api.member.Role;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
public class ContentLikeRepositoryTest {

    @Autowired
    ContentLikeRepository contentLikeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    CelebRepository celebRepository;

    @Autowired
    EntityManager entityManager;

    @MockitoBean
    ArticleRepositoryImpl articleRepository;


    private ContentLike contentLike;
    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Celeb celeb = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("Test Celeb1")
                .profileImageUrl("https://test1.com.profile.jpg")
                .instagramName("test1_insta")
                .build();
        celebRepository.save(celeb);

        Content content = Content.builder()
                .titleKo("테스트 콘텐츠")
                .titleEn("Test Content")
                .originImageUrl("https://test_origin.jpg")
                .summaryKo("테스트 요약")
                .summaryEn("Test Summary")
                .sourceUrl("https://test_source.com")
                .sourceDate(LocalDateTime.now())
                .sourceType(SourceType.INSTAGRAM)
                .celeb(celeb)
                .build();
        contentRepository.save(content);

        contentLike = ContentLike.builder()
                .member(member)
                .content(content)
                .build();
        contentLikeRepository.save(contentLike);
    }

    @Test
    @DisplayName("ContentLike 저장 조회 테스트")
    void contentLikeSaveAndFindTest() {
        ContentLike savedContentLike = contentLikeRepository.findAll().getFirst();
        assertThat(savedContentLike).isNotNull();
        assertThat(savedContentLike.getContent()).isEqualTo(contentLike.getContent());
        assertThat(savedContentLike.getMember()).isEqualTo(contentLike.getMember());
    }

    @Test
    @DisplayName("ContentLike Update Test")
    void contentLikeUpdateTest() {
        Member memberForUpdate = Member.builder()
                .userId("test_for_update")
                .password("test_password")
                .name("테스트 멤버")
                .email("test1@celebstyle.com")
                .role(Role.USER)
                .build();
        memberRepository.save(memberForUpdate);

        contentLike.setMember(memberForUpdate);

        entityManager.flush();
        entityManager.clear();

        ContentLike updatedContentLike = contentLikeRepository.findAll().get(0);

        assertThat(updatedContentLike.getMember().getEmail()).isNotEqualTo(member.getEmail());
        assertThat(updatedContentLike.getContent().getTitleKo()).isEqualTo(contentLike.getContent().getTitleKo());
    }

    @Test
    @DisplayName("ContentLike Delete Test")
    void contentLikeDeleteTest() {
        contentLikeRepository.delete(contentLike);

        ContentLike deletedContentLike = contentLikeRepository.findByMember(member);

        assertThat(deletedContentLike).isNull();
    }
}
