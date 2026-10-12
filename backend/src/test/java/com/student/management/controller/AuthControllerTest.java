package com.student.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.dto.request.LoginRequest;
import com.student.management.dto.response.AuthResponse;
import com.student.management.security.CustomUserDetailsService;
import com.student.management.security.JwtAuthenticationEntryPoint;
import com.student.management.security.JwtAuthenticationFilter;
import com.student.management.security.JwtTokenProvider;
import com.student.management.service.AuthService;
import com.student.management.util.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/auth/login should return 200 with JWT token")
    void testLoginEndpoint_Success() throws Exception {
        LoginRequest request = new LoginRequest("admin@sms.com", "Admin@123");
        AuthResponse authResponse = AuthResponse.builder()
                .token("sample.jwt.token")
                .type("Bearer")
                .email("admin@sms.com")
                .role(RoleType.ROLE_ADMIN)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("sample.jwt.token"))
                .andExpect(jsonPath("$.data.email").value("admin@sms.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login with invalid email format should return 400 Bad Request")
    void testLoginEndpoint_ValidationFailure() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email-format", "pass");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
