package com.celebstyle.api.brand;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BrandTest {

    @Test
    @DisplayName("브랜드 생성 실패 테스트 - 필수 값(이름) 누락 시 예외 발생")
    void createBrand_Fail_Test() {
        assertThatThrownBy(() -> Brand.builder()
                .koreanName("테스트 브랜드")
                .englishName(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot be Empty");

        assertThatThrownBy(() -> Brand.builder()
                .koreanName(null)
                .englishName("Test Brand")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot be Empty");
    }
}
