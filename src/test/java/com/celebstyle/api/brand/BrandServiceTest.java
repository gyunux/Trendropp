package com.celebstyle.api.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.celebstyle.api.brand.dto.BrandCreateRequest;
import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.brand.service.BrandService;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;


    @Test
    @DisplayName("브랜드 생성 - [시나리오 1] 정상적으로 생성 성공")
    void createBrand_Success() {
        BrandCreateRequest request = new BrandCreateRequest();
        ReflectionTestUtils.setField(request, "englishName", "Nike");
        ReflectionTestUtils.setField(request, "koreanName", "나이키");

        Brand savedBrand = Brand.builder()
                .englishName("Nike")
                .koreanName("나이키")
                .build();
        ReflectionTestUtils.setField(savedBrand, "id", 1L);

        given(brandRepository.existsByEnglishName("Nike")).willReturn(false);
        given(brandRepository.save(any(Brand.class))).willReturn(savedBrand);

        BrandView result = brandService.createBrand(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEnglishName()).isEqualTo("Nike");

        verify(brandRepository, times(1)).existsByEnglishName("Nike");
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 생성 - [시나리오 2] 이미 존재하는 영문 이름일 경우 예외 발생")
    void createBrand_Fail_Duplicate() {
        BrandCreateRequest request = new BrandCreateRequest();
        ReflectionTestUtils.setField(request, "englishName", "Nike");
        ReflectionTestUtils.setField(request, "koreanName", "나이키");

        given(brandRepository.existsByEnglishName("Nike")).willReturn(true);

        assertThatThrownBy(() -> brandService.createBrand(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 브랜드입니다");

        verify(brandRepository, times(1)).existsByEnglishName("Nike");
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 페이징 조회 - [시나리오 1] 정상 조회 및 변환")
    void findAll_Page_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Brand brand1 = Brand.builder().englishName("Nike").koreanName("나이키").build();
        ReflectionTestUtils.setField(brand1, "id", 1L);

        Brand brand2 = Brand.builder().englishName("Adidas").koreanName("아디다스").build();
        ReflectionTestUtils.setField(brand2, "id", 2L);

        Page<Brand> mockPage = new PageImpl<>(List.of(brand1, brand2), pageable, 2);

        given(brandRepository.findAll(pageable)).willReturn(mockPage);

        Page<BrandView> result = brandService.findAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getEnglishName()).isEqualTo("Nike");
        assertThat(result.getContent().get(1).getKoreanName()).isEqualTo("아디다스");
    }


    @Test
    @DisplayName("브랜드 전체 이름 조회 - [시나리오 1] 영문 이름 오름차순 조회 성공")
    void findAllBrandsName_Success() {
        // Given
        Brand brand1 = Brand.builder().englishName("Adidas").koreanName("아디다스").build();
        Brand brand2 = Brand.builder().englishName("Nike").koreanName("나이키").build();

        given(brandRepository.findAllByOrderByEnglishNameAsc()).willReturn(List.of(brand1, brand2));

        List<BrandView> result = brandService.findAllBrandsName();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEnglishName()).isEqualTo("Adidas"); // 정렬 순서 확인
        assertThat(result.get(1).getEnglishName()).isEqualTo("Nike");
    }


    @Test
    @DisplayName("브랜드 단건 조회 - [시나리오 1] 존재하는 ID 조회 성공")
    void findById_Success() {
        Brand brand = Brand.builder().englishName("Nike").koreanName("나이키").build();
        ReflectionTestUtils.setField(brand, "id", 1L);

        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));

        BrandView result = brandService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEnglishName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("브랜드 단건 조회 - [시나리오 2] 존재하지 않는 ID 조회 시 예외 발생")
    void findById_Fail_NotFound() {
        given(brandRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("브랜드를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("브랜드 삭제 - [시나리오 1] 존재하는 ID 삭제 성공")
    void deleteBrand_Success() {
        given(brandRepository.existsById(1L)).willReturn(true);

        brandService.deleteBrand(1L);

        verify(brandRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("브랜드 삭제 - [시나리오 2] 존재하지 않는 ID 삭제 시 예외 발생")
    void deleteBrand_Fail_NotFound() {
        given(brandRepository.existsById(999L)).willReturn(false);

        assertThatThrownBy(() -> brandService.deleteBrand(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("브랜드를 찾을 수 없습니다");

        verify(brandRepository, never()).deleteById(any());
    }
}