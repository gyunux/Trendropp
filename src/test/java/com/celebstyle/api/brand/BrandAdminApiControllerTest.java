package com.celebstyle.api.brand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.celebstyle.api.brand.controller.BrandAdminApiController;
import com.celebstyle.api.brand.dto.BrandCreateRequest;
import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.brand.service.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BrandAdminApiController.class)
class BrandAdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

    @Autowired
    private ObjectMapper objectMapper; // 객체 -> JSON 변환용

    @Test
    @DisplayName("[POST] 브랜드 생성 API - 201 Created & Location Header")
    @WithMockUser(roles = "ADMIN")
    void createBrandTest() throws Exception {
        BrandCreateRequest request = new BrandCreateRequest();
        ReflectionTestUtils.setField(request, "englishName", "Nike");
        ReflectionTestUtils.setField(request, "koreanName", "나이키");

        BrandView mockResponse = new BrandView(1L, "Nike", "나이키");

        given(brandService.createBrand(any(BrandCreateRequest.class))).willReturn(mockResponse);

        mockMvc.perform(post("/api/admin/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.englishName").value("Nike"))
                .andExpect(jsonPath("$.koreanName").value("나이키"));
    }

    @Test
    @DisplayName("[GET] 브랜드 전체 목록 조회 API - 200 OK")
    @WithMockUser(roles = "ADMIN")
    void getAllBrandsTest() throws Exception {
        List<BrandView> mockList = List.of(
                new BrandView(1L, "Nike", "나이키"),
                new BrandView(2L, "Adidas", "아디다스")
        );

        given(brandService.findAllBrandsName()).willReturn(mockList);

        mockMvc.perform(get("/api/admin/brands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].englishName").value("Nike"))
                .andExpect(jsonPath("$[1].englishName").value("Adidas"));
    }

    @Test
    @DisplayName("[GET] 브랜드 단건 조회 API - 200 OK")
    @WithMockUser(roles = "ADMIN")
    void getBrandByIdTest() throws Exception {
        Long brandId = 1L;
        BrandView mockResponse = new BrandView(brandId, "Nike", "나이키");

        given(brandService.findById(brandId)).willReturn(mockResponse);

        mockMvc.perform(get("/api/admin/brands/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(brandId))
                .andExpect(jsonPath("$.englishName").value("Nike"));
    }

    @Test
    @DisplayName("[DELETE] 브랜드 삭제 API - 204 No Content")
    @WithMockUser(roles = "ADMIN")
    void deleteBrandTest() throws Exception {
        Long brandId = 1L;
        doNothing().when(brandService).deleteBrand(brandId);

        mockMvc.perform(delete("/api/admin/brands/{id}", brandId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // 3. Verify
        verify(brandService).deleteBrand(brandId);
    }
}