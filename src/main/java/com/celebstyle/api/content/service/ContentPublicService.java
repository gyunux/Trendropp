package com.celebstyle.api.content.service;

import com.celebstyle.api.content.Content;
import com.celebstyle.api.content.ContentRepository;
import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.dto.ContentPublicView;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentPublicService {
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public List<ContentPublicView> findAllForPublicView() {
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadDate");

        return contentRepository.findAll(sort).stream()
                .map(ContentPublicView::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContentDetailView getContentDetail(Long id){
        Content content = contentRepository.findById(id).orElseThrow();

        ContentDetailView contentDetailView = ContentDetailView.fromEntity(content);
        return contentDetailView;
    }
}
