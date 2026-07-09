package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.LoginRequestDTO;
import com.ahmad.jobtracker.dto.request.RegisterRequestDTO;
import com.ahmad.jobtracker.dto.response.AuthResponseDTO;
import com.ahmad.jobtracker.dto.response.UserResponseDTO;
import com.ahmad.jobtracker.entity.User;

public interface UserService {
    AuthResponseDTO register(RegisterRequestDTO dto);
    AuthResponseDTO login(LoginRequestDTO dto);
    UserResponseDTO getProfile(User user);
}