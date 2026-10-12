package com.student.management.service;

import com.student.management.dto.request.LoginRequest;
import com.student.management.dto.request.RegisterRequest;
import com.student.management.dto.response.AuthResponse;
import com.student.management.entity.User;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.repository.UserRepository;
import com.student.management.security.JwtTokenProvider;
import com.student.management.security.UserPrincipal;
import com.student.management.service.impl.AuthServiceImpl;
import com.student.management.util.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthServiceImpl authService;

    private User sampleUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        authService = new AuthServiceImpl(authenticationManager, userRepository, passwordEncoder, tokenProvider, modelMapper);

        sampleUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_pass")
                .fullName("Test User")
                .role(RoleType.ROLE_STUDENT)
                .build();

        userPrincipal = new UserPrincipal(
                1L,
                "test@example.com",
                "encoded_pass",
                "Test User",
                RoleType.ROLE_STUDENT,
                Collections.emptyList()
        );
    }

    @Test
    @DisplayName("Login should return AuthResponse with JWT token when credentials are valid")
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password@123");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt.mock.token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.mock.token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo(RoleType.ROLE_STUDENT);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Login should throw BadCredentialsException when credentials are invalid")
    void testLogin_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Register should create user and return AuthResponse with token")
    void testRegister_Success() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("New User")
                .email("new@example.com")
                .password("Password@123")
                .role(RoleType.ROLE_STUDENT)
                .build();

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateTokenFromUsername(anyString(), anyString())).thenReturn("new.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new.jwt.token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register should throw DuplicateResourceException if email is already taken")
    void testRegister_DuplicateEmail() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullName("Existing User")
                .email("test@example.com")
                .password("Password@123")
                .role(RoleType.ROLE_STUDENT)
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
