package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.request.LoginRequestDTO;
import com.ahmad.jobtracker.dto.request.RegisterRequestDTO;
import com.ahmad.jobtracker.dto.response.AuthResponseDTO;
import com.ahmad.jobtracker.dto.response.UserResponseDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        AuthResponseDTO response = userService.register(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        AuthResponseDTO response = userService.login(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(@AuthenticationPrincipal User user) {
        UserResponseDTO response = userService.getProfile(user);
        return ResponseEntity.ok(response);
    }
}