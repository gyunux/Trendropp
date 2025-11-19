package com.celebstyle.api.celeb;

import static org.assertj.core.api.Assertions.assertThat;

import com.celebstyle.api.article.repository.ArticleRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest(
        includeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CelebRepository.class // 오직 이 클래스만 로드하도록 지정
        )
)
public class CelebRepositoryTest {

    @Autowired
    CelebRepository celebRepository;

    @MockitoBean
    ArticleRepositoryImpl articleRepositoryImpl;

    private Celeb validCeleb;

    @BeforeEach
    void setUp() {
        validCeleb = Celeb.builder()
                .nameKo("테스트 셀럽")
                .nameEn("Test Celeb")
                .profileImageUrl("https://test.com.profile.jpg")
                .instagramName("test_insta")
                .build();
    }

    @Test
    void createCelebTest() {
        Celeb savedCeleb = celebRepository.save(validCeleb);

        assertThat(savedCeleb).isNotNull();
        assertThat(savedCeleb.getNameKo()).isEqualTo("테스트 셀럽");
        assertThat(savedCeleb.getNameEn()).isEqualTo("Test Celeb");
        assertThat(savedCeleb.getProfileImageUrl()).isEqualTo("https://test.com.profile.jpg");
        assertThat(savedCeleb.getInstagramName()).isEqualTo("test_insta");

    }
}
