package com.ahmad.jobtracker.service.implementation;

import com.ahmad.jobtracker.dto.request.ContactRequestDTO;
import com.ahmad.jobtracker.dto.response.ContactResponseDTO;
import com.ahmad.jobtracker.entity.Contact;
import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.repository.ContactRepository;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ahmad.jobtracker.exception.ResourceNotFoundException;
import com.ahmad.jobtracker.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Override
    public ContactResponseDTO create(Long applicationId, ContactRequestDTO dto, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(applicationId))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", applicationId);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - application: {} by user: {}", applicationId, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        Contact contact = Contact.builder()
                .jobApplication(app)
                .name(dto.getName())
                .email(dto.getEmail())
                .linkedinUrl(dto.getLinkedinUrl())
                .notes(dto.getNotes())
                .build();

        Contact saved = contactRepository.save(Objects.requireNonNull(contact));
        log.info("Contact created - id: {} for application: {}", saved.getId(), applicationId);
        return convertToDTO(saved);
    }

    @Override
    public List<ContactResponseDTO> getAllByApplication(Long applicationId, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(applicationId))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", applicationId);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - application: {} by user: {}", applicationId, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        return contactRepository.findByJobApplication(app)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactResponseDTO update(Long id, ContactRequestDTO dto, User user) {
        Contact contact = contactRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Contact not found - id: {}", id);
                    return new ResourceNotFoundException("Contact not found");
                });

        if (!contact.getJobApplication().getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - contact: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        if (dto.getName() != null) contact.setName(dto.getName());
        if (dto.getEmail() != null) contact.setEmail(dto.getEmail());
        if (dto.getLinkedinUrl() != null) contact.setLinkedinUrl(dto.getLinkedinUrl());
        if (dto.getNotes() != null) contact.setNotes(dto.getNotes());

        Contact saved = contactRepository.save(Objects.requireNonNull(contact));
        log.info("Contact updated - id: {}", id);
        return convertToDTO(saved);
    }

    @Override
    public void delete(Long id, User user) {
        Contact contact = contactRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Contact not found - id: {}", id);
                    return new ResourceNotFoundException("Contact not found");
                });

        if (!contact.getJobApplication().getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - contact: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        log.info("Contact deleted - id: {}", id);
        contactRepository.delete(Objects.requireNonNull(contact));
    }

    private ContactResponseDTO convertToDTO(Contact contact) {
        return ContactResponseDTO.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .linkedinUrl(contact.getLinkedinUrl())
                .notes(contact.getNotes())
                .build();
    }
}