package com.celebstyle.api.like;

import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.ContentRepository;
import com.celebstyle.api.content.dto.ContentPublicView;
import com.celebstyle.api.member.Member;
import com.celebstyle.api.member.MemberRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentLikeService {

    private final ContentLikeRepository contentLikeRepository;
    private final MemberRepository memberRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void addLike(Long memberId, Long contentId) {
        Optional<ContentLike> existingLike = contentLikeRepository.findByMemberIdAndContentId(memberId, contentId);
        if (existingLike.isPresent()) {
            return;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("콘텐츠를 찾을 수 없습니다."));

        ContentLike newLike = ContentLike.builder()
                .member(member)
                .content(content)
                .build();
        contentLikeRepository.save(newLike);
    }

    @Transactional(readOnly = true)
    public List<ContentPublicView> getMyLikes(Long memberId, Locale locale) {
        List<ContentLike> myLikes = contentLikeRepository.findByMemberIdWithContentAndCeleb(memberId);

        return myLikes.stream()
                .map(ContentLike::getContent)
                .map(content -> ContentPublicView.fromEntity(content, true, locale))
                .toList();
    }

    @Transactional
    public void removeLike(Long memberId, Long contentId) {
        Optional<ContentLike> like = contentLikeRepository.findByMemberIdAndContentId(memberId, contentId);

        like.ifPresent(contentLikeRepository::delete);
    }
}
