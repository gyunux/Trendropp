package com.celebstyle.api.celeb;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.celebstyle.api.celeb.dto.CelebAdminView;
import com.celebstyle.api.celeb.dto.CelebCreateRequest;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import com.celebstyle.api.celeb.dto.CelebUpdateRequest;
import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.celeb.service.CelebService;
import com.celebstyle.api.common.S3UploadService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CelebServiceTest {

    @InjectMocks
    private CelebService celebService;

    @Mock
    private CelebRepository celebRepository;

    @Mock
    private S3UploadService s3UploadService;

    @Test
    @DisplayName("셀럽 생성 서비스 로직 테스트")
    void createCelebServiceTest() throws IOException {

        String expectedImageUrl = "https://celebstyletest-storage.s3.ap-northeast-2.amazonaws.com/celebs-test.jpg";

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage", "test.jpg", "image/jpeg", "test data".getBytes());

        CelebCreateRequest celebCreateRequest = new CelebCreateRequest();
        celebCreateRequest.setNameKo("아이유");
        celebCreateRequest.setNameEn("IU");
        celebCreateRequest.setInstagramName("dlwlrma");
        celebCreateRequest.setProfileImage(mockFile);

        when(s3UploadService.upload(eq(mockFile), eq("celebs"))).thenReturn(expectedImageUrl);

        Celeb createCeleb = Celeb.builder()
                .nameKo(celebCreateRequest.getNameKo())
                .nameEn(celebCreateRequest.getNameEn())
                .instagramName(celebCreateRequest.getInstagramName())
                .profileImageUrl(expectedImageUrl)
                .build();
        createCeleb.setId(1L);

        when(celebRepository.save(any(Celeb.class))).thenReturn(createCeleb);

        CelebCreateResponse response = celebService.create(celebCreateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProfileImageUrl()).isEqualTo(expectedImageUrl);
        assertThat(response.getNameKo()).isEqualTo("아이유");

        verify(s3UploadService, times(1)).upload(eq(mockFile), eq("celebs"));

        verify(celebRepository, times(1)).save(any(Celeb.class));
    }

    @Test
    @DisplayName("관리자용 셀럽 리스트 페이징 조회 테스트")
    void findPaginatedForAdminViewTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Celeb celeb1 = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("test_celeb1")
                .instagramName("test1_insta")
                .profileImageUrl("https://test1.com/profile.jpg")
                .build();

        Celeb celeb2 = Celeb.builder()
                .nameKo("테스트 셀럽2")
                .nameEn("test_celeb2")
                .instagramName("test2_insta")
                .profileImageUrl("https://test2.com/profile.jpg")
                .build();

        Celeb celeb3 = Celeb.builder()
                .nameKo("테스트 셀럽3")
                .nameEn("test_celeb3")
                .instagramName("test3_insta")
                .profileImageUrl("https://test3.com/profile.jpg")
                .build();

        ReflectionTestUtils.setField(celeb1, "id", 1L);
        ReflectionTestUtils.setField(celeb2, "id", 2L);
        ReflectionTestUtils.setField(celeb3, "id", 3L);

        List<Celeb> mockEntityList = List.of(celeb1, celeb2, celeb3);

        Page<Celeb> mockPage = new PageImpl<>(mockEntityList, pageable, mockEntityList.size());

        when(celebRepository.findAll(pageable)).thenReturn(mockPage);

        Page<CelebAdminView> result = celebService.findPaginatedForAdminView(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);

        assertThat(result.getContent().get(0)).isInstanceOf(CelebAdminView.class);
        assertThat(result.getContent().get(0).getNameKo()).isEqualTo("테스트 셀럽1");

        verify(celebRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("관리자 콘텐츠 생성 페이지 셀럽 리스트")
    void findAllCelebForContentCreate() {
        Celeb celeb1 = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("test_celeb1")
                .instagramName("test1_insta")
                .profileImageUrl("https://test1.com/profile.jpg")
                .build();

        Celeb celeb2 = Celeb.builder()
                .nameKo("테스트 셀럽2")
                .nameEn("test_celeb2")
                .instagramName("test2_insta")
                .profileImageUrl("https://test2.com/profile.jpg")
                .build();

        Celeb celeb3 = Celeb.builder()
                .nameKo("테스트 셀럽3")
                .nameEn("test_celeb3")
                .instagramName("test3_insta")
                .profileImageUrl("https://test3.com/profile.jpg")
                .build();

        ReflectionTestUtils.setField(celeb1, "id", 1L);
        ReflectionTestUtils.setField(celeb2, "id", 2L);
        ReflectionTestUtils.setField(celeb3, "id", 3L);

        List<Celeb> mockEntityList = List.of(celeb1, celeb2, celeb3);

        when(celebRepository.findAll()).thenReturn(mockEntityList);
        List<CelebView> result = celebService.findAllForCelebsName();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getName()).isEqualTo("테스트 셀럽1");
        verify(celebRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("관리자 페이지 셀럽 수정 테스트(이미지 변경 O)")
    void celebUpdateWithProfileImageTest() throws IOException {
        Celeb celeb1 = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("test_celeb1")
                .instagramName("test1_insta")
                .profileImageUrl("https://test1.com/profile.jpg")
                .build();

        ReflectionTestUtils.setField(celeb1, "id", 1L);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage", "test.jpg", "image/jpeg", "test data".getBytes());

        CelebUpdateRequest request = new CelebUpdateRequest();

        request.setNameKo("테스트 셀러브1"); // 이름만 변경
        request.setNameEn("test_celeb1");
        request.setInstagramName("test1_insta");
        request.setProfileImage(mockFile);

        when(celebRepository.findById(1L)).thenReturn(Optional.of(celeb1));

        celebService.update(1L, request);

        assertThat(celeb1.getNameKo()).isEqualTo("테스트 셀러브1");

        verify(celebRepository, times(1)).findById(1L);


    }

    @Test
    @DisplayName("관리자 페이지 셀럽 수정 테스트(이미지 변경 X)")
    void celebUpdateWithoutProfileImageTest() throws IOException {
        Celeb celeb1 = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("test_celeb1")
                .instagramName("test1_insta")
                .profileImageUrl("https://test1.com/profile.jpg")
                .build();

        ReflectionTestUtils.setField(celeb1, "id", 1L);

        CelebUpdateRequest request = new CelebUpdateRequest();

        request.setNameKo("테스트 셀러브1"); // 이름만 변경
        request.setNameEn("test_celeb1");
        request.setInstagramName("test1_insta");
        request.setProfileImage(null);

        when(celebRepository.findById(1L)).thenReturn(Optional.of(celeb1));

        celebService.update(1L, request);

        assertThat(celeb1.getNameKo()).isEqualTo("테스트 셀러브1");
        verify(s3UploadService, never()).upload(any(), any());
        verify(celebRepository, times(1)).findById(1L);


    }

    @Test
    @DisplayName("관리자 페이지 셀럽 수정 테스트(빈 파일이 넘어왔을 때 - 이미지 변경 X)")
    void celebUpdateWithEmptyProfileImageTest() throws IOException {

        Celeb celeb1 = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("test_celeb1")
                .instagramName("test1_insta")
                .profileImageUrl("https://test1.com/original.jpg")
                .build();

        ReflectionTestUtils.setField(celeb1, "id", 1L);

        CelebUpdateRequest request = new CelebUpdateRequest();
        request.setNameKo("테스트 셀러브1");
        request.setNameEn("test_celeb1");
        request.setInstagramName("test1_insta");

        MockMultipartFile emptyFile = new MockMultipartFile(
                "profileImage",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        request.setProfileImage(emptyFile);

        when(celebRepository.findById(1L)).thenReturn(Optional.of(celeb1));

        celebService.update(1L, request);

        assertThat(celeb1.getNameKo()).isEqualTo("테스트 셀러브1");

        verify(s3UploadService, never()).upload(any(), any());

    }

    @Test
    @DisplayName("셀럽 삭제 서비스 테스트 Throw X")
    void celebDeleteTest() {
        when(celebRepository.existsById(1L)).thenReturn(true);

        celebService.delete(1L);

        verify(celebRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("셀럽 삭제 서비스 테스트 Throw O")
    void celebDeleteWithThrowTest() {
        when(celebRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> celebService.delete(1L)) // 1. 이 메서드를 실행했을 때
                .isInstanceOf(EntityNotFoundException.class)     // 2. 이 예외가 터져야 하고
                .hasMessageContaining("해당 ID의 셀럽을 찾을 수 없습니다"); // 3. 메시지는 이걸 포함해야 한다.

        verify(celebRepository, never()).deleteById(1L);

    }
}
