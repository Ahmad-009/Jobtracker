package com.ahmad.jobtracker.service.implementation;

import com.ahmad.jobtracker.dto.request.LoginRequestDTO;
import com.ahmad.jobtracker.dto.request.RegisterRequestDTO;
import com.ahmad.jobtracker.dto.response.AuthResponseDTO;
import com.ahmad.jobtracker.dto.response.UserResponseDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.repository.UserRepository;
import com.ahmad.jobtracker.service.JwtService;
import com.ahmad.jobtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ahmad.jobtracker.exception.EmailAlreadyExistsException;
import java.util.Objects;
import com.ahmad.jobtracker.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        log.info("Registration attempt for: {}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registration failed - email exists: {}", dto.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(Objects.requireNonNull(user));

        String token = jwtService.generateToken(savedUser);

        log.info("User registered successfully - id: {}", savedUser.getId());
        return AuthResponseDTO.builder()
                .token(token)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        log.info("Login attempt for: {}", dto.getEmail());
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password for: {}", dto.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        log.info("Login successful for: {}", dto.getEmail());
        return AuthResponseDTO.builder()
            .token(token)
            .userId(user.getId())
            .name(user.getName())
            .build();
    }

    @Override
    public UserResponseDTO getProfile(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .build();
    }
}