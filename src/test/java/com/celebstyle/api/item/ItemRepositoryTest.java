package com.celebstyle.api.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.celebstyle.api.article.repository.ArticleRepositoryImpl;
import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.brand.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BrandRepository brandRepository;

    @MockitoBean
    ArticleRepositoryImpl articleRepositoryImpl;

    private Brand savedBrand;

    @BeforeEach
    void setUp() {
        Brand brand = Brand.builder()
                .englishName("Nike")
                .koreanName("나이키")
                .build();
        savedBrand = brandRepository.save(brand);
    }

    @Test
    @DisplayName("아이템 정상 생성 테스트")
    void createItem_Success() {
        Item item = Item.builder()
                .name("에어 포스 1")
                .imageUrl("https://image.url/af1.jpg")
                .productUrl("https://shop.url/af1")
                .brand(savedBrand)
                .build();

        Item savedItem = itemRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("에어 포스 1");
        assertThat(savedItem.getBrand().getEnglishName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("아이템 생성 실패 - 이름(Name)이 Null일 경우 객체 생성 단계에서 예외 발생")
    void createItem_Fail_NameNull() {
        // save()가 아니라 builder().build() 시점에 예외를 검증해야 합니다.
        assertThatThrownBy(() -> Item.builder()
                .name(null)
                .imageUrl("https://image.url/test.jpg")
                .productUrl("https://shop.url/test")
                .brand(savedBrand)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("필수 값이 누락되었습니다.");
    }

    @Test
    @DisplayName("아이템 생성 실패 - 이미지URL(ImageUrl)이 Null일 경우 객체 생성 단계에서 예외 발생")
    void createItem_Fail_ImageUrlNull() {
        assertThatThrownBy(() -> Item.builder()
                .name("테스트 아이템")
                .imageUrl(null)
                .productUrl("https://shop.url/test")
                .brand(savedBrand)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("필수 값이 누락되었습니다.");
    }

    @Test
    @DisplayName("아이템 생성 실패 - 상품URL(ProductUrl)이 Null일 경우 객체 생성 단계에서 예외 발생")
    void createItem_Fail_ProductUrlNull() {
        assertThatThrownBy(() -> Item.builder()
                .name("테스트 아이템")
                .imageUrl("https://image.url/test.jpg")
                .productUrl(null)
                .brand(savedBrand)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("필수 값이 누락되었습니다.");
    }
}