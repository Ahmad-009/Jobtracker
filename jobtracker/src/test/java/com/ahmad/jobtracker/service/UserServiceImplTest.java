package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.LoginRequestDTO;
import com.ahmad.jobtracker.dto.request.RegisterRequestDTO;
import com.ahmad.jobtracker.dto.response.AuthResponseDTO;
import com.ahmad.jobtracker.dto.response.UserResponseDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.exception.EmailAlreadyExistsException;
import com.ahmad.jobtracker.exception.InvalidCredentialsException;
import com.ahmad.jobtracker.repository.UserRepository;
import com.ahmad.jobtracker.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private User user;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterRequestDTO("Ahmad", "ahmad@gmail.com", "password123");
        loginDTO = new LoginRequestDTO("ahmad@gmail.com", "password123");
        user = User.builder()
                .id(1L)
                .name("Ahmad")
                .email("ahmad@gmail.com")
                .password("hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        AuthResponseDTO response = userService.register(registerDTO);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("Ahmad", response.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(registerDTO));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        AuthResponseDTO response = userService.login(loginDTO);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("Ahmad", response.getName());
    }

    @Test
    void login_userNotFound_throwsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginDTO));
    }

    @Test
    void login_wrongPassword_throwsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginDTO));
    }

    @Test
    void getProfile_returnsCorrectDTO() {
        UserResponseDTO response = userService.getProfile(user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ahmad", response.getName());
        assertEquals("ahmad@gmail.com", response.getEmail());
    }
}