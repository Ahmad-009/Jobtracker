package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.request.ApplicationRequestDTO;
import com.ahmad.jobtracker.dto.request.ApplicationUpdateDTO;
import com.ahmad.jobtracker.dto.response.ApplicationDetailDTO;
import com.ahmad.jobtracker.dto.response.ApplicationSummaryDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    @Autowired
    private JobApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationSummaryDTO> create(
            @Valid @RequestBody ApplicationRequestDTO dto,
            @AuthenticationPrincipal User user) {
        ApplicationSummaryDTO response = applicationService.create(dto, user);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationSummaryDTO>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationSummaryDTO> response = applicationService.getAll(user, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDetailDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ApplicationDetailDTO response = applicationService.getById(id, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationSummaryDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationUpdateDTO dto,
            @AuthenticationPrincipal User user) {
        ApplicationSummaryDTO response = applicationService.update(id, dto, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        applicationService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}