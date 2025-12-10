package com.celebstyle.api.celeb.service;

import com.celebstyle.api.celeb.dto.CelebViewStatDto;
import com.celebstyle.api.content.ContentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatService {

    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public List<CelebViewStatDto> getCelebViewStats() {
        return contentRepository.getCelebViewStats();
    }
}
