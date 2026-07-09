package com.ahmad.jobtracker.service.implementation;

import com.ahmad.jobtracker.dto.request.ApplicationRequestDTO;
import com.ahmad.jobtracker.dto.request.ApplicationUpdateDTO;
import com.ahmad.jobtracker.dto.response.ApplicationDetailDTO;
import com.ahmad.jobtracker.dto.response.ApplicationSummaryDTO;
import com.ahmad.jobtracker.dto.response.ContactResponseDTO;
import com.ahmad.jobtracker.dto.response.InterviewResponseDTO;
import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.repository.ContactRepository;
import com.ahmad.jobtracker.repository.InterviewRepository;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.ahmad.jobtracker.exception.ResourceNotFoundException;
import com.ahmad.jobtracker.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Override
    public ApplicationSummaryDTO create(ApplicationRequestDTO dto, User user) {
        JobApplication app = JobApplication.builder()
                .user(user)
                .companyName(dto.getCompanyName())
                .jobTitle(dto.getJobTitle())
                .jobUrl(dto.getJobUrl())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .jobType(dto.getJobType())
                .experienceRequired(dto.getExperienceRequired())
                .workType(dto.getWorkType())
                .domain(dto.getDomain())
                .salaryMin(dto.getSalaryMin())
                .salaryMax(dto.getSalaryMax())
                .appliedDate(dto.getAppliedDate())
                .notes(dto.getNotes())
                .build();

        JobApplication saved = applicationRepository.save(Objects.requireNonNull(app));
        log.info("Application created - id: {} for user: {}", saved.getId(), user.getId());
        return convertToSummaryDTO(saved);
    }

    @Override
    public Page<ApplicationSummaryDTO> getAll(User user, int page, int size) {  
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return applicationRepository.findByUser(user, pageable)
            .map(this::convertToSummaryDTO);
}

    @Override
    public ApplicationDetailDTO getById(Long id, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", id);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access attempt - application: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        log.info("Application fetched - id: {} by user: {}", id, user.getId());

        List<InterviewResponseDTO> interviews = interviewRepository
                .findByJobApplication(app)
                .stream()
                .map(interview -> InterviewResponseDTO.builder()
                        .id(interview.getId())
                        .interviewDate(interview.getInterviewDate())
                        .type(interview.getType())
                        .notes(interview.getNotes())
                        .outcome(interview.getOutcome())
                        .build())
                .collect(Collectors.toList());

        List<ContactResponseDTO> contacts = contactRepository
                .findByJobApplication(app)
                .stream()
                .map(contact -> ContactResponseDTO.builder()
                        .id(contact.getId())
                        .name(contact.getName())
                        .email(contact.getEmail())
                        .linkedinUrl(contact.getLinkedinUrl())
                        .notes(contact.getNotes())
                        .build())
                .collect(Collectors.toList());

        return ApplicationDetailDTO.builder()
                .id(app.getId())
                .companyName(app.getCompanyName())
                .jobTitle(app.getJobTitle())
                .jobUrl(app.getJobUrl())
                .status(app.getStatus())
                .priority(app.getPriority())
                .jobType(app.getJobType())
                .experienceRequired(app.getExperienceRequired())
                .workType(app.getWorkType())
                .domain(app.getDomain())
                .salaryMin(app.getSalaryMin())
                .salaryMax(app.getSalaryMax())
                .appliedDate(app.getAppliedDate())
                .notes(app.getNotes())
                .createdAt(app.getCreatedAt())
                .interviews(interviews)
                .contacts(contacts)
                .build();
    }

    @Override
    public ApplicationSummaryDTO update(Long id, ApplicationUpdateDTO dto, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", id);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access - application: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        Optional.ofNullable(dto.getCompanyName()).ifPresent(app::setCompanyName);
        Optional.ofNullable(dto.getJobTitle()).ifPresent(app::setJobTitle);
        Optional.ofNullable(dto.getJobUrl()).ifPresent(app::setJobUrl);
        Optional.ofNullable(dto.getStatus()).ifPresent(app::setStatus);
        Optional.ofNullable(dto.getPriority()).ifPresent(app::setPriority);
        Optional.ofNullable(dto.getJobType()).ifPresent(app::setJobType);
        Optional.ofNullable(dto.getExperienceRequired()).ifPresent(app::setExperienceRequired);
        Optional.ofNullable(dto.getWorkType()).ifPresent(app::setWorkType);
        Optional.ofNullable(dto.getDomain()).ifPresent(app::setDomain);
        Optional.ofNullable(dto.getSalaryMin()).ifPresent(app::setSalaryMin);
        Optional.ofNullable(dto.getSalaryMax()).ifPresent(app::setSalaryMax);
        Optional.ofNullable(dto.getAppliedDate()).ifPresent(app::setAppliedDate);
        Optional.ofNullable(dto.getNotes()).ifPresent(app::setNotes);

        JobApplication saved = applicationRepository.save(Objects.requireNonNull(app));
        log.info("Application updated - id: {} by user: {}", id, user.getId());
        return convertToSummaryDTO(saved);
    }

    @Override
    public void delete(Long id, User user) {
        JobApplication app = applicationRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    log.warn("Application not found - id: {}", id);
                    return new ResourceNotFoundException("Application not found");
                });

        if (!app.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access attempt - application: {} by user: {}", id, user.getId());
            throw new UnauthorizedException("Not authorized");
        }

        log.info("Application deleted - id: {} by user: {}", id, user.getId());
        applicationRepository.delete(Objects.requireNonNull(app));
    }

    private ApplicationSummaryDTO convertToSummaryDTO(JobApplication app) {
        return ApplicationSummaryDTO.builder()
                .id(app.getId())
                .companyName(app.getCompanyName())
                .jobTitle(app.getJobTitle())
                .jobUrl(app.getJobUrl())
                .status(app.getStatus())
                .priority(app.getPriority())
                .jobType(app.getJobType())
                .experienceRequired(app.getExperienceRequired())
                .workType(app.getWorkType())
                .domain(app.getDomain())
                .salaryMin(app.getSalaryMin())
                .salaryMax(app.getSalaryMax())
                .appliedDate(app.getAppliedDate())
                .notes(app.getNotes())
                .createdAt(app.getCreatedAt())
                .build();
    }
}