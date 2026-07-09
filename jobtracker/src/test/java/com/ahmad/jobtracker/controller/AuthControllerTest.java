package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.request.LoginRequestDTO;
import com.ahmad.jobtracker.dto.request.RegisterRequestDTO;
import com.ahmad.jobtracker.dto.response.AuthResponseDTO;
import com.ahmad.jobtracker.exception.EmailAlreadyExistsException;
import com.ahmad.jobtracker.exception.InvalidCredentialsException;
import com.ahmad.jobtracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void register_success_returns201() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO("Ahmad", "ahmad@gmail.com", "password123");
        AuthResponseDTO response = new AuthResponseDTO("mockToken", 1L, "Ahmad");

        when(userService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.name").value("Ahmad"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO("Ahmad", "ahmad@gmail.com", "password123");

        when(userService.register(any())).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void register_missingFields_returns400() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO("", "notanemail", "123");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_success_returns200() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("ahmad@gmail.com", "password123");
        AuthResponseDTO response = new AuthResponseDTO("mockToken", 1L, "Ahmad");

        when(userService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("ahmad@gmail.com", "wrongpassword");

        when(userService.login(any())).thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}