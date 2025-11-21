package com.celebstyle.api.brand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.celebstyle.api.brand.controller.BrandAdminController;
import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.brand.service.BrandService;
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

@WebMvcTest(BrandAdminController.class)
class BrandAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

    @Test
    @DisplayName("[Dashboard] 기본 조회 (0페이지) - 정상 응답 및 네비게이션 바 계산 확인")
    @WithMockUser(roles = "ADMIN")
    void getBrandDashboard_Default() throws Exception {
        List<BrandView> content = List.of(new BrandView(1L, "Nike", "나이키"));
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<BrandView> mockPage = new PageImpl<>(content, pageRequest, 100);

        given(brandService.findAll(any(Pageable.class))).willReturn(mockPage);

        mockMvc.perform(get("/admin/brands")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/brands"))
                .andExpect(model().attribute("currentPage", "brands"))
                .andExpect(model().attributeExists("brandsPage"))

                .andExpect(model().attribute("startPage", 0))
                .andExpect(model().attribute("endPage", 4));
    }

    @Test
    @DisplayName("[Dashboard] 데이터 없음 - startPage/endPage 0 처리 확인")
    @WithMockUser(roles = "ADMIN")
    void getBrandDashboard_Empty() throws Exception {
        Page<BrandView> emptyPage = Page.empty(PageRequest.of(0, 20));

        given(brandService.findAll(any(Pageable.class))).willReturn(emptyPage);

        mockMvc.perform(get("/admin/brands"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("startPage", 0))
                .andExpect(model().attribute("endPage", 0));
    }

    @Test
    @DisplayName("[Dashboard] 마지막 페이지 블록 보정 로직 확인")
    @WithMockUser(roles = "ADMIN")
    void getBrandDashboard_LastPageShift() throws Exception {
        int totalElements = 140;
        int pageIndex = 6;
        PageRequest pageRequest = PageRequest.of(pageIndex, 20);
        Page<BrandView> mockPage = new PageImpl<>(List.of(), pageRequest, totalElements);

        given(brandService.findAll(any(Pageable.class))).willReturn(mockPage);

        mockMvc.perform(get("/admin/brands")
                        .param("page", String.valueOf(pageIndex))
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("startPage", 2))
                .andExpect(model().attribute("endPage", 6));
    }
}