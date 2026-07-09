package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.response.StatsDTO;
import com.ahmad.jobtracker.entity.User;

public interface StatsService {
    StatsDTO getStats(User user);
}