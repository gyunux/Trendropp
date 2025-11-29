package com.celebstyle.api.celeb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.celebstyle.api.article.repository.ArticleRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private Celeb preSavedCeleb;

    @BeforeEach
    void setUp() {
        validCeleb = Celeb.builder()
                .nameKo("테스트 셀럽1")
                .nameEn("Test Celeb1")
                .profileImageUrl("https://test1.com.profile.jpg")
                .instagramName("test1_insta")
                .build();

        preSavedCeleb = Celeb.builder()
                .nameKo("테스트 셀럽2")
                .nameEn("Test Celeb2")
                .profileImageUrl("https://test2.com.profile.jpg")
                .instagramName("test2_insta")
                .build();
        preSavedCeleb = celebRepository.save(preSavedCeleb);
    }

    @Test
    @DisplayName("셀럽 생성 테스트")
    void celebCreateTest() {
        Celeb savedCeleb = celebRepository.save(validCeleb);

        assertThat(savedCeleb).isNotNull();
        assertThat(savedCeleb.getNameKo()).isEqualTo("테스트 셀럽1");
        assertThat(savedCeleb.getNameEn()).isEqualTo("Test Celeb1");
        assertThat(savedCeleb.getProfileImageUrl()).isEqualTo("https://test1.com.profile.jpg");
        assertThat(savedCeleb.getInstagramName()).isEqualTo("test1_insta");
    }

    @Test
    @DisplayName("셀럽 조회 테스트")
    void celebFindTest() {
        Celeb findedCeleb = celebRepository.findByNameKo("테스트 셀럽2");

        assertThat(findedCeleb).isNotNull();
        assertThat(findedCeleb.getNameKo()).isEqualTo("테스트 셀럽2");
        assertThat(findedCeleb.getNameEn()).isEqualTo("Test Celeb2");
        assertThat(findedCeleb.getProfileImageUrl()).isEqualTo("https://test2.com.profile.jpg");
        assertThat(findedCeleb.getInstagramName()).isEqualTo("test2_insta");
    }

    @Test
    @DisplayName("셀럽 수정 테스트")
    void celebUpdateTest() {
        preSavedCeleb.setNameEn("Test Two Celeb");
        String newNameEn = "Test Two Celeb Updated";
        String newNameKo = "테스트 셀럽 2 수정됨";

        preSavedCeleb.setNameEn(newNameEn);
        preSavedCeleb.setNameKo(newNameKo);

        Celeb updatedCeleb = celebRepository.save(preSavedCeleb);

        Long celebId = updatedCeleb.getId();

        Celeb foundCeleb = celebRepository.findById(celebId).orElse(null);

        assertThat(foundCeleb).isNotNull();

        assertThat(foundCeleb.getNameEn()).isEqualTo(newNameEn);
        assertThat(foundCeleb.getNameKo()).isEqualTo(newNameKo);

        assertThat(foundCeleb.getInstagramName()).isEqualTo("test2_insta");
    }

    @Test
    @DisplayName("셀럽 삭제 테스트")
    void celebDeleteTest() {
        celebRepository.delete(preSavedCeleb);

        Celeb deletedCeleb = celebRepository.findById(2L).orElse(null);

        assertThat(deletedCeleb).isNull();
    }

    @Test
    @DisplayName("한국 이름이 누락되면 셀럽 생성 시 예외가 발생")
    void createCeleb_Exception_Test1() {
        assertThatThrownBy(() -> Celeb.builder()
                .nameKo(null)
                .nameEn("IU")
                .profileImageUrl("url")
                .instagramName("insta")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot be Empty");
    }

    @Test
    @DisplayName("영어 이름이 누락되면 셀럽 생성 시 예외가 발생")
    void createCeleb_Exception_Test2() {
        assertThatThrownBy(() -> Celeb.builder()
                .nameKo("아이유")
                .nameEn(null)
                .profileImageUrl("url")
                .instagramName("insta")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot be Empty");
    }

    @Test
    @DisplayName("프로필 사진이 누락되면 셀럽 생성 시 예외가 발생")
    void createCeleb_Exception_Test3() {
        assertThatThrownBy(() -> Celeb.builder()
                .nameKo("아이유")
                .nameEn("IU")
                .profileImageUrl(null)
                .instagramName("insta")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot be Empty");
    }

    @Test
    @DisplayName("인스타그램이 누락되면 셀럽 생성 시 예외가 발생")
    void createCeleb_Exception_Test4() {
        assertThatThrownBy(() -> Celeb.builder()
                .nameKo("아이유")
                .nameEn("IU")
                .profileImageUrl("url")
                .instagramName(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot be Empty");
    }
}
