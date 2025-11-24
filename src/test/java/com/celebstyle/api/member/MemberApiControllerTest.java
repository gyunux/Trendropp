package com.celebstyle.api.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.celebstyle.api.member.controller.MemberApiController;
import com.celebstyle.api.member.dto.EmailChangeRequest;
import com.celebstyle.api.member.dto.MemberDeactivateRequest;
import com.celebstyle.api.member.dto.MemberSignupRequest;
import com.celebstyle.api.member.dto.PasswordChangeRequest;
import com.celebstyle.api.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberApiController.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    private CustomUserDetails mockUserDetails;
    private Long mockMemberId;

    @BeforeEach
    void setUp() {
        mockMemberId = 1L;
        Member member = Member.builder()
                .userId("testUser")
                .role(Role.USER)
                .password("encodedPassword")
                .build();
        ReflectionTestUtils.setField(member, "id", mockMemberId);

        mockUserDetails = new CustomUserDetails(member);
    }

    @Test
    @DisplayName("회원가입 API 성공 테스트")
    void signup_Success() throws Exception {
        MemberSignupRequest req = new MemberSignupRequest();
        ReflectionTestUtils.setField(req, "userId", "newUser");
        ReflectionTestUtils.setField(req, "password", "password123!");
        ReflectionTestUtils.setField(req, "name", "신규회원");
        ReflectionTestUtils.setField(req, "email", "new@test.com");

        mockMvc.perform(post("/api/members/signup")
                        .with(csrf())
                        .with(user("guest"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(memberService).signUp(any(MemberSignupRequest.class));
    }

    @Test
    @DisplayName("아이디 중복 확인 API - 사용 가능 (중복 아님)")
    void checkUsername_Available() throws Exception {
        String userId = "uniqueUser";
        given(memberService.isUsernameDuplicated(userId)).willReturn(false);

        mockMvc.perform(get("/api/members/check-userid")
                        .param("userId", userId)
                        .with(csrf())
                        .with(user("guest")))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("이메일 중복 확인 API - 사용 불가 (중복임)")
    void checkEmail_Unavailable() throws Exception {
        String email = "duplicate@test.com";
        given(memberService.isEmailDuplicated(email)).willReturn(true);

        mockMvc.perform(get("/api/members/check-email")
                        .param("email", email)
                        .with(user("guest")))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("이메일 변경 API 성공 - 로그인된 사용자")
    void changeEmail_Success() throws Exception {
        EmailChangeRequest req = new EmailChangeRequest();
        req.setCurrentPassword("currentPass");
        req.setNewEmail("new@test.com");
        mockMvc.perform(patch("/api/members/email")
                        .with(csrf())
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(memberService).changeEmail(eq(mockMemberId), any(EmailChangeRequest.class));
    }

    @Test
    @DisplayName("비밀번호 변경 API 성공")
    void changePassword_Success() throws Exception {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setCurrentPassword("oldPass1");
        req.setNewPassword("newPass1");

        mockMvc.perform(patch("/api/members/password")
                        .with(csrf())
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(memberService).changePassword(eq(mockMemberId), any(PasswordChangeRequest.class));
    }

    @Test
    @DisplayName("회원 탈퇴 API 성공 - 세션 무효화 확인")
    void deactivateMember_Success() throws Exception {
        MemberDeactivateRequest req = new MemberDeactivateRequest();
        ReflectionTestUtils.setField(req, "currentPassword", "validPassword");

        mockMvc.perform(delete("/api/members/me")
                        .with(csrf())
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(memberService).deactivateMember(mockMemberId, "validPassword");

    }
}