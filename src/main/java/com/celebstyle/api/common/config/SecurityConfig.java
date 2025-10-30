package com.celebstyle.api.common.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // [규칙 1] (가장 엄격) 관리자 전용 경로
                                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                                // [규칙 2] (인증 필요) 로그인을 해야만 사용 가능한 기능
                                // '마이페이지'와 '콘텐츠 찜하기' API
                                .requestMatchers(
                                        "/mypage/**",
                                        "/api/contents/*/like" // 찜하기 API 예시
                                ).authenticated() // USER, ADMIN 모두 가능 (로그인만 하면 됨)

                                // [규칙 3] (모두 허용)
                                // 회원가입/로그인 API, H2 콘솔, 정적 리소스(CSS, JS)는 인증 없이 누구나 접근 가능
                                .requestMatchers(
                                        "/login", "/signup",
                                        "/api/members/login", "/api/members/signup",
                                        "/api/members/check-userid", "/api/members/check-email",
                                        "/css/**", "/js/**", "/images/**", "/"
                                ).permitAll()
                                .requestMatchers(toH2Console()).permitAll() // H2 콘솔 허용

                                // [규칙 4] (콘텐츠 구경 허용)
                                // 위에서 걸러지지 않은 '모든 GET 요청' (홈페이지, 콘텐츠 상세/목록 조회 등)은 누구나 접근 가능
                                .requestMatchers(HttpMethod.GET).permitAll()

                                // [규칙 5] (기본 규칙)
                                // 위에 명시되지 않은 나머지 모든 요청 (예: POST /api/comment 등)은 인증이 필요함
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(toH2Console()) // H2 콘솔 경로에 대해서는 CSRF 비활성화
                        .disable() // (임시로 전체 비활성화, 나중에 개발 완료 후 세부 설정 권장)
                )

                // [핵심 추가 3] H2 콘솔이 iframe에서 열릴 수 있도록 X-Frame-Options 비활성화
                .headers(headers ->
                        headers.frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();
    }
}
