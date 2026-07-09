package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.request.InterviewRequestDTO;
import com.ahmad.jobtracker.dto.response.InterviewResponseDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications/{applicationId}/interviews")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponseDTO> create(
            @PathVariable Long applicationId,
            @Valid @RequestBody InterviewRequestDTO dto,
            @AuthenticationPrincipal User user) {
        InterviewResponseDTO response = interviewService.create(applicationId, dto, user);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InterviewResponseDTO>> getAll(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User user) {
        List<InterviewResponseDTO> response = interviewService.getAllByApplication(applicationId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterviewResponseDTO> update(
            @PathVariable Long applicationId,
            @PathVariable Long id,
            @Valid @RequestBody InterviewRequestDTO dto,
            @AuthenticationPrincipal User user) {
        InterviewResponseDTO response = interviewService.update(id, dto, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long applicationId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        interviewService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}