package com.celebstyle.api.content.service;

import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.ContentRepository;
import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.dto.ContentPublicView;
import com.celebstyle.api.like.ContentLikeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentPublicService {
    private final ContentRepository contentRepository;
    private final ContentLikeRepository contentLikeRepository;

    @Transactional(readOnly = true)
    public List<ContentPublicView> findAllForMainPage(Long currentMemberId, Locale locale) {
        Set<Long> likedContentIds = new HashSet<>();
        if (currentMemberId != null) {
            likedContentIds = contentLikeRepository.findLikedContentIdsByMemberId(currentMemberId);
        }

        List<Content> contents = contentRepository.findAllWithCelebOrderByUploadDateDesc();

        Set<Long> finalLikedContentIds = likedContentIds;

        return contents.stream()
                .map(content -> {
                    boolean isLiked = finalLikedContentIds.contains(content.getId());
                    return ContentPublicView.fromEntity(content, isLiked, locale);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ContentDetailView getContentDetail(Long contentId, Long currentMemberId, Locale locale) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("콘텐츠를 찾을 수 없습니다."));

        boolean isLiked = false;
        if (currentMemberId != null) {
            isLiked = contentLikeRepository.existsByMemberIdAndContentId(currentMemberId, contentId);
        }

        return ContentDetailView.fromEntity(content, isLiked, locale);
    }
}
