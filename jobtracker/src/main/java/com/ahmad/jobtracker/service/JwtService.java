package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.entity.User;

public interface JwtService {

    String generateToken(User user);
    String extractEmail(String token);
    boolean isTokenValid(String token);
}