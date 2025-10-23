package com.celebstyle.api.content;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.ArticleRepository;
import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import com.celebstyle.api.common.S3UploadService;
import com.celebstyle.api.item.Item;
import com.celebstyle.api.item.ItemService;
import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.content.dto.ContentAdminView;
import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.dto.SaveContentRequest;
import com.celebstyle.api.contentitem.ContentItem;
import com.celebstyle.api.contentitem.ContentItemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentAdminService {
    private final ContentRepository contentRepository;
    private final CelebRepository celebRepository;
    private final ItemService itemService;
    private final ContentItemRepository contentItemRepository;
    private final ArticleRepository articleRepository; // ArticleRepository 주입
    private final S3UploadService s3UploadService;

    @Transactional
    public ContentAdminView createContent(SaveContentRequest request) throws IOException {
        Celeb celeb = celebRepository.findById(request.getCelebId()).orElseThrow();

        String imageUrl = s3UploadService.upload(request.getMainImageFile(),"contents");

        Content newContent = Content.builder()
                .title(request.getTitle())
                .originImageUrl(imageUrl)
                .summary(request.getSummary())
                .sourceUrl(request.getSourceUrl())
                .sourceDate(request.getSourceDate())
                .sourceType(request.getSourceType())
                .celeb(celeb)
                .build();
        contentRepository.save(newContent);

        if (request.getItems() != null) {
            for (ItemRequest itemDto : request.getItems()) {
                Item newItem = itemService.createItem(itemDto);
                ContentItem contentItem = new ContentItem(newContent, newItem);
                contentItemRepository.save(contentItem);
            }
        }
        if (request.getSourceArticleId() != null) {
            Article article = articleRepository.findById(request.getSourceArticleId()).orElseThrow();
            article.markAsProcessed();
        }

        return ContentAdminView.fromEntity(newContent);
    }

    @Transactional(readOnly = true)
    public List<ContentAdminView> findAll() {
        return contentRepository.findAll().stream()
                .map(ContentAdminView::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContentDetailView getContent(Long id){
        Content content = contentRepository.findById(id).orElseThrow();
        return ContentDetailView.fromEntity(content);
    }

    @Transactional
    public void updateContent(Long contentId, SaveContentRequest request) throws IOException {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("착장을 찾을 수 없습니다."));

        Celeb celeb = celebRepository.findById(request.getCelebId()).orElseThrow();
        content.setCeleb(celeb);
//        content.setOriginImageUrl(request.getMainImageUrl());
        content.setSourceType(request.getSourceType());
        content.setSourceUrl(request.getSourceUrl());
        content.setSourceDate(request.getSourceDate());

        contentItemRepository.deleteAllByContent(content);

        for (ItemRequest itemDto : request.getItems()) {
            Item item = itemService.updateOrCreateItem(itemDto);

            ContentItem contentItem = new ContentItem(content, item);
            contentItemRepository.save(contentItem);
        }
    }

    @Transactional
    public void deleteContent(Long id) {
        if (!contentRepository.existsById(id)) {
            throw new EntityNotFoundException("착장정보를 찾을 수 없습니다: " + id);
        }
        contentRepository.deleteById(id);
//        Content content = contentRepository.findById(id).orElseThrow();
//        content.setDeleted(true);
    }

}
