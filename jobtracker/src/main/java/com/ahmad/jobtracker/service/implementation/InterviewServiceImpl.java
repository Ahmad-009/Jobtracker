package com.ahmad.jobtracker.service.implementation;

import com.ahmad.jobtracker.dto.request.InterviewRequestDTO;
import com.ahmad.jobtracker.dto.response.InterviewResponseDTO;
import com.ahmad.jobtracker.entity.Interview;
import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.repository.InterviewRepository;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.InterviewService;
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
public class InterviewServiceImpl implements InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Override
    public InterviewResponseDTO create(Long applicationId, InterviewRequestDTO dto, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(applicationId))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", applicationId);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - application: {} by user: {}", applicationId, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        Interview interview = Interview.builder()
                .jobApplication(app)
                .interviewDate(dto.getInterviewDate())
                .type(dto.getType())
                .notes(dto.getNotes())
                .outcome(dto.getOutcome())
                .build();

        Interview saved = interviewRepository.save(Objects.requireNonNull(interview));
        log.info("Interview created - id: {} for application: {}", saved.getId(), applicationId);
        return convertToDTO(saved);
    }

    @Override
    public List<InterviewResponseDTO> getAllByApplication(Long applicationId, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(applicationId))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", applicationId);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - application: {} by user: {}", applicationId, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        return interviewRepository.findByJobApplication(app)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InterviewResponseDTO update(Long id, InterviewRequestDTO dto, User user) {
        Interview interview = interviewRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Interview not found - id: {}", id);
                    return new ResourceNotFoundException("Interview not found");
                });

        if (!interview.getJobApplication().getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - interview: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        if (dto.getInterviewDate() != null) interview.setInterviewDate(dto.getInterviewDate());
        if (dto.getType() != null) interview.setType(dto.getType());
        if (dto.getNotes() != null) interview.setNotes(dto.getNotes());
        if (dto.getOutcome() != null) interview.setOutcome(dto.getOutcome());

        Interview saved = interviewRepository.save(Objects.requireNonNull(interview));
        log.info("Interview updated - id: {}", id);
        return convertToDTO(saved);
    }

    @Override
    public void delete(Long id, User user) {
        Interview interview = interviewRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Interview not found - id: {}", id);
                    return new ResourceNotFoundException("Interview not found");
                });

        if (!interview.getJobApplication().getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - interview: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        log.info("Interview deleted - id: {}", id);
        interviewRepository.delete(interview);
    }

    private InterviewResponseDTO convertToDTO(Interview interview) {
        return InterviewResponseDTO.builder()
                .id(interview.getId())
                .interviewDate(interview.getInterviewDate())
                .type(interview.getType())
                .notes(interview.getNotes())
                .outcome(interview.getOutcome())
                .build();
    }
}