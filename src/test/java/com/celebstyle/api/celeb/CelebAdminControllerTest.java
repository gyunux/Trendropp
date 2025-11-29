package com.celebstyle.api.celeb;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.celebstyle.api.celeb.controller.CelebAdminController;
import com.celebstyle.api.celeb.dto.CelebAdminView;
import com.celebstyle.api.celeb.dto.CelebViewStatDto;
import com.celebstyle.api.celeb.service.CelebService;
import com.celebstyle.api.celeb.service.StatService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CelebAdminController.class)
class CelebAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CelebService celebService;

    @MockitoBean
    private StatService statService;

    @Test
    @DisplayName("[시나리오 1] 데이터가 0건일 때 - startPage, endPage는 0이어야 한다")
    @WithMockUser(roles = "ADMIN")
    void getCelebList_EmptyData() throws Exception {
        Page<CelebAdminView> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        given(celebService.findPaginatedForAdminView(any(Pageable.class))).willReturn(emptyPage);

        mockMvc.perform(get("/admin/celebs"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/celebs"))
                .andExpect(model().attribute("startPage", 0))
                .andExpect(model().attribute("endPage", 0));
    }

    @Test
    @DisplayName("[시나리오 2] 일반적인 조회(0페이지) - startPage=0, endPage=4 (네비게이션 5개)")
    @WithMockUser(roles = "ADMIN")
    void getCelebList_NormalStart() throws Exception {
        List<CelebAdminView> content = List.of(new CelebAdminView(1L, "img", "test", "test", "test"));
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CelebAdminView> normalPage = new PageImpl<>(content, pageRequest, 100);

        given(celebService.findPaginatedForAdminView(any(Pageable.class))).willReturn(normalPage);

        mockMvc.perform(get("/admin/celebs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("startPage", 0))
                .andExpect(model().attribute("endPage", 4));
    }

    @Test
    @DisplayName("[시나리오 3] 마지막 페이지 블록 보정 - 총 7페이지 중 마지막(6)페이지 조회 시 startPage=2")
    @WithMockUser(roles = "ADMIN")
    void getCelebList_LastPageAdjustment() throws Exception {
        PageRequest pageRequest = PageRequest.of(6, 10);
        Page<CelebAdminView> lastBlockPage = new PageImpl<>(List.of(), pageRequest, 70);

        given(celebService.findPaginatedForAdminView(any(Pageable.class))).willReturn(lastBlockPage);

        mockMvc.perform(get("/admin/celebs")
                        .param("page", "6")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("startPage", 2))
                .andExpect(model().attribute("endPage", 6));
    }

    @Test
    @DisplayName("통계 페이지 조회 - 모델 데이터 및 뷰 이름 검증")
    @WithMockUser(roles = "ADMIN")
    void getStatsPageTest() throws Exception {
        List<CelebViewStatDto> mockStatsList = List.of(
                new CelebViewStatDto(1L, "아이유", "IU", 1500L),
                new CelebViewStatDto(2L, "박보영", "BY", 1200L),
                new CelebViewStatDto(3L, "뉴진스", "NJZ", 900L)
        );

        given(statService.getCelebViewStats()).willReturn(mockStatsList);

        mockMvc.perform(get("/admin/stats"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(view().name("admin/stats"))

                .andExpect(model().attribute("currentPage", "stats"))

                .andExpect(model().attributeExists("statsList"))
                .andExpect(model().attribute("statsList", hasSize(3)))
                .andExpect(model().attribute("statsList", mockStatsList));

        verify(statService).getCelebViewStats();
    }
}