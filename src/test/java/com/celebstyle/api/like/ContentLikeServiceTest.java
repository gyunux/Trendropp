package com.celebstyle.api.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.ContentRepository;
import com.celebstyle.api.content.dto.ContentPublicView;
import com.celebstyle.api.member.Member;
import com.celebstyle.api.member.MemberRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContentLikeServiceTest {

    @InjectMocks
    private ContentLikeService contentLikeService;

    @Mock
    private ContentLikeRepository contentLikeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ContentRepository contentRepository;

    @Test
    @DisplayName("좋아요 추가 성공 - 기존에 없던 경우")
    void addLike_Success() {
        Long memberId = 1L;
        Long contentId = 100L;

        Member member = Member.builder().build();
        Content content = Content.builder().build();

        given(contentLikeRepository.findByMemberIdAndContentId(memberId, contentId))
                .willReturn(Optional.empty());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));

        contentLikeService.addLike(memberId, contentId);

        then(contentLikeRepository).should().save(any(ContentLike.class));
    }

    @Test
    @DisplayName("좋아요 추가 무시 - 이미 좋아요가 존재하는 경우 (중복 방지)")
    void addLike_Skip_IfAlreadyExists() {
        Long memberId = 1L;
        Long contentId = 100L;

        ContentLike existingLike = ContentLike.builder().build();

        given(contentLikeRepository.findByMemberIdAndContentId(memberId, contentId))
                .willReturn(Optional.of(existingLike));

        contentLikeService.addLike(memberId, contentId);

        then(contentLikeRepository).should(never()).save(any());

        then(memberRepository).should(never()).findById(any());
        then(contentRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 회원이 없는 경우")
    void addLike_Fail_MemberNotFound() {
        Long memberId = 1L;
        Long contentId = 100L;

        given(contentLikeRepository.findByMemberIdAndContentId(memberId, contentId))
                .willReturn(Optional.empty());
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> contentLikeService.addLike(memberId, contentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 콘텐츠가 없는 경우")
    void addLike_Fail_ContentNotFound() {
        Long memberId = 1L;
        Long contentId = 100L;
        Member member = Member.builder().build();

        given(contentLikeRepository.findByMemberIdAndContentId(memberId, contentId))
                .willReturn(Optional.empty());
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(contentRepository.findById(contentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> contentLikeService.addLike(memberId, contentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("콘텐츠를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("내 좋아요 목록 조회 - 한국어(Locale.KOREAN) 요청 시 한국어 제목 반환")
    void getMyLikes_Korean() {
        Long memberId = 1L;
        Locale locale = Locale.KOREA;

        Celeb celeb = Celeb.builder()
                .nameKo("아이유")
                .nameEn("IU")
                .profileImageUrl("https://test.jpg")
                .instagramName("test_insta")
                .build();

        Content content = Content.builder()
                .titleKo("가을 패션")
                .titleEn("Autumn Fashion")
                .celeb(celeb)
                .build();

        ContentLike like = ContentLike.builder()
                .member(Member.builder().name("test").build())
                .content(content)
                .build();

        given(contentLikeRepository.findByMemberIdWithContentAndCeleb(memberId))
                .willReturn(List.of(like));

        List<ContentPublicView> result = contentLikeService.getMyLikes(memberId, locale);

        assertThat(result).hasSize(1);
        ContentPublicView view = result.get(0);

        assertThat(view.getTitle()).isEqualTo("가을 패션");
        assertThat(view.isLiked()).isTrue();
    }

    @Test
    @DisplayName("내 좋아요 목록 조회 - 영어(Locale.ENGLISH) 요청 시 영어 제목 반환")
    void getMyLikes_English() {

        Long memberId = 1L;
        Locale locale = Locale.ENGLISH;

        Celeb celeb = Celeb.builder()
                .nameKo("아이유")
                .nameEn("IU")
                .profileImageUrl("https://test.jpg")
                .instagramName("test_insta")
                .build();

        Content content = Content.builder()
                .titleKo("가을 패션")
                .titleEn("Autumn Fashion")
                .celeb(celeb)
                .build();

        ContentLike like = ContentLike.builder().content(content).build();

        given(contentLikeRepository.findByMemberIdWithContentAndCeleb(memberId))
                .willReturn(List.of(like));

        List<ContentPublicView> result = contentLikeService.getMyLikes(memberId, locale);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Autumn Fashion");
    }


    @Test
    @DisplayName("좋아요 취소 성공 - 존재하는 좋아요 삭제")
    void removeLike_Success() {
        Long memberId = 1L;
        Long contentId = 100L;
        ContentLike existingLike = ContentLike.builder().build();

        given(contentLikeRepository.findByMemberIdAndContentId(memberId, contentId))
                .willReturn(Optional.of(existingLike));

        contentLikeService.removeLike(memberId, contentId);

        then(contentLikeRepository).should().delete(existingLike);
    }

    @Test
    @DisplayName("좋아요 취소 무시 - 존재하지 않는 좋아요 (에러 없이 종료)")
    void removeLike_Skip_IfNotFound() {
        Long memberId = 1L;
        Long contentId = 999L;

        given(contentLikeRepository.findByMemberIdAndContentId(memberId, contentId))
                .willReturn(Optional.empty());

        contentLikeService.removeLike(memberId, contentId);

        then(contentLikeRepository).should(never()).delete(any());
    }
}
