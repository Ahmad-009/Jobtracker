package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.response.StatsDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping
    public ResponseEntity<StatsDTO> getStats(@AuthenticationPrincipal User user) {
        StatsDTO stats = statsService.getStats(user);
        return ResponseEntity.ok(stats);
    }
}