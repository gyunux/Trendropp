package com.celebstyle.api.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.celebstyle.api.member.controller.MyPageController;
import com.celebstyle.api.member.dto.MemberMyPageView;
import com.celebstyle.api.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MyPageController.class)
public class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("마이페이지 조회 성공 - 로그인된 사용자 정보 기반")
    void getMemberMyPage_Success() throws Exception {
        Long memberId = 1L;
        Member member = Member.builder()
                .userId("test")
                .password("test_password")
                .name("테스트 멤버")
                .email("test@celebstyle.com")
                .role(Role.USER)
                .build();

        org.springframework.test.util.ReflectionTestUtils.setField(member, "id", memberId);

        CustomUserDetails userDetails = new CustomUserDetails(member);

        MemberMyPageView responseView = new MemberMyPageView(member);

        given(memberService.getMemberMyPage(memberId)).willReturn(responseView);

        mockMvc.perform(get("/mypage")
                        .with(user(userDetails)))
                .andExpect(view().name("mypage"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attribute("member", responseView));
    }
}
