package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.ApplicationRequestDTO;
import com.ahmad.jobtracker.dto.response.ApplicationSummaryDTO;
import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.entity.enums.*;
import com.ahmad.jobtracker.exception.ResourceNotFoundException;
import com.ahmad.jobtracker.exception.UnauthorizedException;
import com.ahmad.jobtracker.repository.ContactRepository;
import com.ahmad.jobtracker.repository.InterviewRepository;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.implementation.JobApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @InjectMocks
    private JobApplicationServiceImpl applicationService;

    private User user;
    private User otherUser;
    private JobApplication application;
    private ApplicationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Ahmad")
                .email("ahmad@gmail.com")
                .password("hashed")
                .build();

        otherUser = User.builder()
                .id(2L)
                .name("Ali")
                .email("ali@gmail.com")
                .password("hashed")
                .build();

        application = JobApplication.builder()
                .id(1L)
                .user(user)
                .companyName("Google")
                .jobTitle("Backend Engineer")
                .status(ApplicationStatus.APPLIED)
                .priority(Priority.HIGH)
                .jobType(JobType.FULL_TIME)
                .experienceRequired(ExperienceLevel.FRESH)
                .workType(WorkType.REMOTE)
                .appliedDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        requestDTO = new ApplicationRequestDTO(
                "Google", "Backend Engineer", null,
                ApplicationStatus.APPLIED, Priority.HIGH,
                JobType.FULL_TIME, ExperienceLevel.FRESH,
                WorkType.REMOTE, null, null, null,
                LocalDate.now(), null
        );
    }

    @Test
    void create_success() {
        when(applicationRepository.save(any(JobApplication.class))).thenReturn(application);

        ApplicationSummaryDTO response = applicationService.create(requestDTO, user);

        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
        verify(applicationRepository).save(any(JobApplication.class));
    }

    @Test
    void getAll_returnsPaginatedResults() {
        List<JobApplication> applications = List.of(application);
        Page<JobApplication> page = new PageImpl<>(applications);
        when(applicationRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(page);

        Page<ApplicationSummaryDTO> result = applicationService.getAll(user, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Google", result.getContent().get(0).getCompanyName());
    }

    @Test
    void getById_success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.findByJobApplication(any())).thenReturn(List.of());
        when(contactRepository.findByJobApplication(any())).thenReturn(List.of());

        var response = applicationService.getById(1L, user);

        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
    }

    @Test
    void getById_notFound_throwsException() {
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> applicationService.getById(999L, user));
    }

    @Test
    void getById_unauthorizedUser_throwsException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> applicationService.getById(1L, otherUser));
    }

    @Test
    void delete_success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertDoesNotThrow(() -> applicationService.delete(1L, user));
        verify(applicationRepository).delete(application);
    }

    @Test
    void delete_unauthorizedUser_throwsException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> applicationService.delete(1L, otherUser));
        verify(applicationRepository, never()).delete(any(JobApplication.class));
    }
}