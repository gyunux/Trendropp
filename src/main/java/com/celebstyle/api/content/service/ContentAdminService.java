package com.celebstyle.api.content.service;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.repository.ArticleRepository;
import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.ContentRepository;
import com.celebstyle.api.content.dto.ContentAdminView;
import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.dto.ContentViewForEdit;
import com.celebstyle.api.content.dto.SaveContentRequest;
import com.celebstyle.api.content.dto.UpdateContentRequest;
import com.celebstyle.api.contentitem.ContentItem;
import com.celebstyle.api.contentitem.ContentItemRepository;
import com.celebstyle.api.item.Item;
import com.celebstyle.api.item.ItemRepository;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.item.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ItemRepository itemRepository;

    @Transactional
    public ContentAdminView createContent(SaveContentRequest request) throws IOException {
        Celeb celeb = celebRepository.findById(request.getCelebId()).orElseThrow();
        Content newContent = Content.builder()
                .titleKo(request.getTitleKo())     // [수정]
                .titleEn(request.getTitleEn())     // [추가]
                .summaryKo(request.getSummaryKo()) // [수정]
                .summaryEn(request.getSummaryEn()) // [추가]
                .originImageUrl(request.getMainImageUrl())
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
    public Page<ContentAdminView> findAll(Pageable pageable) {
        Page<Content> contentPage = contentRepository.findAll(pageable);
        return contentPage.map(ContentAdminView::fromEntity);
    }

    @Transactional(readOnly = true)
    public ContentDetailView getContent(Long id, Locale locale) {
        Content content = contentRepository.findById(id).orElseThrow();
        return ContentDetailView.fromEntity(content, false, locale);
    }

    @Transactional
    public void updateContent(Long contentId, UpdateContentRequest request) throws IOException {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("착장을 찾을 수 없습니다."));

        Celeb celeb = celebRepository.findById(request.getCelebId()).orElseThrow();
        content.setCeleb(celeb);
        content.setOriginImageUrl(request.getMainImageUrl());
        content.setSourceType(request.getSourceType());
        content.setSourceUrl(request.getSourceUrl());
        content.updateTranslations(
                request.getTitleKo(),
                request.getTitleEn(),
                request.getSummaryKo(),
                request.getSummaryEn());
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
            throw new EntityNotFoundException("콘텐츠를 찾을 수 없습니다: " + id);
        }
        contentRepository.deleteById(id);
//        Content content = contentRepository.findById(id).orElseThrow();
//        content.setDeleted(true);
    }

    @Transactional(readOnly = true)
    public ContentViewForEdit getContentForEdit(Long id) {
        Content content = contentRepository.findById(id).orElseThrow();
        List<Item> items = itemRepository.findItemsByContentId(id);
        List<ItemDetailView> itemDetailViews = new ArrayList<>();

        for (Item item : items) {
            itemDetailViews.add(ItemDetailView.fromEntity(item));
        }
        Celeb celeb = content.getCeleb();
        CelebView celebView = new CelebView(celeb);

        return ContentViewForEdit.fromEntity(content, celebView, itemDetailViews);
    }

}
