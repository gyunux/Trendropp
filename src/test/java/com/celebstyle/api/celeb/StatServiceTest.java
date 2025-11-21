package com.celebstyle.api.celeb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.celebstyle.api.celeb.dto.CelebViewStatDto;
import com.celebstyle.api.celeb.service.StatService;
import com.celebstyle.api.content.ContentRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StatServiceTest {

    @InjectMocks
    private StatService statService;

    @Mock
    private ContentRepository contentRepository;

    @Test
    @DisplayName("셀럽 조회 수 통계 테스트")
    void getCelebViewStatsTest() {
        List<CelebViewStatDto> celebViewStatDtos = List.of(
                new CelebViewStatDto()
        );

        when(contentRepository.getCelebViewStats()).thenReturn(celebViewStatDtos);

        List<CelebViewStatDto> celebViewStatDtos1 = statService.getCelebViewStats();

        assertThat(celebViewStatDtos1.size()).isEqualTo(1);
        verify(contentRepository, times(1)).getCelebViewStats();

    }
}
