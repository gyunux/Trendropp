package com.celebstyle.api.celeb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.celebstyle.api.celeb.controller.CelebAdminApiController;
import com.celebstyle.api.celeb.dto.CelebCreateRequest;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import com.celebstyle.api.celeb.dto.CelebUpdateRequest;
import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.celeb.service.CelebService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CelebAdminApiController.class)
public class CelebAdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CelebService celebService;

    @Test
    @DisplayName("[POST] 셀럽 생성 성공 - 201 Created & Location Header")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCelebSuccessTest() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage", "test.jpg", "image/jpeg", "test data".getBytes());

        CelebCreateResponse response = CelebCreateResponse.builder()
                .id(1L)
                .nameKo("테스트 셀럽")
                .nameEn("test celeb")
                .profileImageUrl("https://s3...")
                .instagramName("test insta")
                .build();

        given(celebService.create(any(CelebCreateRequest.class))).willReturn(response);

        mockMvc.perform(multipart("/api/admin/celebs")
                        .file(mockFile)
                        .param("nameKo", "테스트 셀럽")
                        .param("nameEn", "test celeb")
                        .param("instagramName", "test insta")
                        .with(csrf()))

                .andDo(print())

                .andExpect(status().isCreated())

                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/admin/celebs/1")))

                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nameKo").value("테스트 셀럽"));

        verify(celebService).create(any(CelebCreateRequest.class));
    }

    @Test
    @DisplayName("[GET] 셀럽 리스트 조회 - 200 OK")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCelebViewListTest() throws Exception {

        Celeb celeb1 = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("Test Celeb1")
                .profileImageUrl("https://test1.com.profile.jpg")
                .instagramName("test1_insta")
                .build();

        Celeb celeb2 = Celeb.builder()
                .nameKo("테스트 셀럽2")
                .nameEn("Test Celeb2")
                .profileImageUrl("https://test2.com.profile.jpg")
                .instagramName("test2_insta")
                .build();

        ReflectionTestUtils.setField(celeb1, "id", 1L);
        ReflectionTestUtils.setField(celeb2, "id", 2L);

        List<CelebView> mockResponse = List.of(
                new CelebView(celeb1),
                new CelebView(celeb2)
        );

        given(celebService.findAllForCelebsName()).willReturn(mockResponse);

        mockMvc.perform(get("/api/admin/celebs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("[PUT] 셀럽 수정 - 204 No Content")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCelebTest() throws Exception {
        Long celebId = 1L;

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "update.jpg",
                "image/jpeg",
                "updated data".getBytes()
        );

        doNothing().when(celebService).update(eq(celebId), any(CelebUpdateRequest.class));

        mockMvc.perform(multipart("/api/admin/celebs/{id}", celebId)
                        .file(mockFile)
                        .param("nameKo", "수정된 이름")
                        .param("nameEn", "Updated Name")
                        .param("instagramName", "updated_insta")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(celebService).update(eq(celebId), any(CelebUpdateRequest.class));
    }

    @Test
    @DisplayName("[DELETE] 셀럽 삭제 - 204 NoContent")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCelebTest() throws Exception {
        Long celebId = 1L;

        doNothing().when(celebService).delete(celebId);

        mockMvc.perform(delete("/api/admin/celebs/{id}", celebId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(celebService, times(1)).delete(celebId);

    }
}
