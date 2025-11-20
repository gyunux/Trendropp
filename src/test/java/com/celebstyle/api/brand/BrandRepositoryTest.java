package com.celebstyle.api.brand;

import static org.assertj.core.api.Assertions.assertThat;

import com.celebstyle.api.article.repository.ArticleRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
public class BrandRepositoryTest {

    @Autowired
    BrandRepository brandRepository;

    @MockitoBean
    ArticleRepositoryImpl articleRepositoryImpl;

    private Brand validBrand;
    private Brand preSavedBrand;

    @BeforeEach
    void setUp() {
        validBrand = Brand.builder()
                .koreanName("테스트 브랜드1")
                .englishName("Test Brand1")
                .build();

        preSavedBrand = Brand.builder()
                .koreanName("테스트 브랜드2")
                .englishName("Test Brand2")
                .build();
        preSavedBrand = brandRepository.save(preSavedBrand);
    }

    @Test
    @DisplayName("브랜드 생성 테스트")
    void brandCreateTest() {
        Brand savedBrand = brandRepository.save(validBrand);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getKoreanName()).isEqualTo("테스트 브랜드1");
        assertThat(savedBrand.getEnglishName()).isEqualTo("Test Brand1");
    }

    @Test
    @DisplayName("브랜드 조회 테스트")
    void brandFindTest() {
        Brand findedBrand = brandRepository.findByEnglishName("Test Brand2");

        assertThat(findedBrand).isNotNull();
        assertThat(findedBrand.getKoreanName()).isEqualTo("테스트 브랜드2");
        assertThat(findedBrand.getEnglishName()).isEqualTo("Test Brand2");
    }

    @Test
    @DisplayName("브랜드 수정 테스트")
    void brandUpdateTest() {
        preSavedBrand.setEnglishName("Test Brand Two");

        Brand updatedBrand = brandRepository.save(preSavedBrand);
        assertThat(updatedBrand.getEnglishName()).isEqualTo("Test Brand Two");
        assertThat(updatedBrand.getKoreanName()).isEqualTo("테스트 브랜드2");
    }

    @Test
    @DisplayName("브랜드 삭제 테스트")
    void brandDeleteTest() {
        brandRepository.delete(preSavedBrand);

        Brand deletedBrand = brandRepository.findByEnglishName("Test Brand Two");

        assertThat(deletedBrand).isNull();
    }
}
