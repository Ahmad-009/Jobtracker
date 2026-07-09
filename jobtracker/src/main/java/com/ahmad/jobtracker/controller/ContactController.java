package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.request.ContactRequestDTO;
import com.ahmad.jobtracker.dto.response.ContactResponseDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications/{applicationId}/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponseDTO> create(
            @PathVariable Long applicationId,
            @Valid @RequestBody ContactRequestDTO dto,
            @AuthenticationPrincipal User user) {
        ContactResponseDTO response = contactService.create(applicationId, dto, user);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContactResponseDTO>> getAll(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User user) {
        List<ContactResponseDTO> response = contactService.getAllByApplication(applicationId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> update(
            @PathVariable Long applicationId,
            @PathVariable Long id,
            @Valid @RequestBody ContactRequestDTO dto,
            @AuthenticationPrincipal User user) {
        ContactResponseDTO response = contactService.update(id, dto, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long applicationId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        contactService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}