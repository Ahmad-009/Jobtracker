package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.InterviewRequestDTO;
import com.ahmad.jobtracker.dto.response.InterviewResponseDTO;
import com.ahmad.jobtracker.entity.Interview;
import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.entity.enums.*;
import com.ahmad.jobtracker.exception.ResourceNotFoundException;
import com.ahmad.jobtracker.exception.UnauthorizedException;
import com.ahmad.jobtracker.repository.InterviewRepository;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.implementation.InterviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class InterviewServiceImplTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private JobApplicationRepository applicationRepository;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private User user;
    private User otherUser;
    private JobApplication application;
    private Interview interview;
    private InterviewRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("Ahmad")
                .email("ahmad@gmail.com")
                .password("hashed").build();

        otherUser = User.builder()
                .id(2L).name("Ali")
                .email("ali@gmail.com")
                .password("hashed").build();

        application = JobApplication.builder()
                .id(1L).user(user)
                .companyName("Google")
                .jobTitle("Backend Engineer")
                .status(ApplicationStatus.APPLIED)
                .priority(Priority.HIGH)
                .jobType(JobType.FULL_TIME)
                .experienceRequired(ExperienceLevel.FRESH)
                .workType(WorkType.REMOTE)
                .appliedDate(LocalDate.now())
                .build();

        interview = Interview.builder()
                .id(1L)
                .jobApplication(application)
                .interviewDate(LocalDateTime.now())
                .type(InterviewType.TECHNICAL)
                .notes("Went well")
                .outcome("Passed")
                .build();

        requestDTO = new InterviewRequestDTO(
                LocalDateTime.now(),
                InterviewType.TECHNICAL,
                "Went well",
                "Passed"
        );
    }

    @Test
    void create_success() {
        ArgumentCaptor<Interview> interviewCaptor = ArgumentCaptor.forClass(Interview.class);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        InterviewResponseDTO response = interviewService.create(1L, requestDTO, user);

        assertNotNull(response);
        assertEquals(InterviewType.TECHNICAL, response.getType());
        verify(interviewRepository).save(interviewCaptor.capture());
        Interview capturedInterview = interviewCaptor.getValue();
        assertNotNull(capturedInterview, "captured interview");
        assertEquals(InterviewType.TECHNICAL, capturedInterview.getType());
    }

    @Test
    void create_applicationNotFound_throwsException() {
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> interviewService.create(999L, requestDTO, user));
    }

    @Test
    void create_unauthorized_throwsException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> interviewService.create(1L, requestDTO, otherUser));
    }

    @Test
    void getAllByApplication_success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.findByJobApplication(any(JobApplication.class))).thenReturn(List.of(interview));

        List<InterviewResponseDTO> result = interviewService.getAllByApplication(1L, user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(InterviewType.TECHNICAL, result.get(0).getType());
    }

    @Test
    void getAllByApplication_unauthorized_throwsException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> interviewService.getAllByApplication(1L, otherUser));
    }

    @Test
    void update_success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        InterviewResponseDTO response = interviewService.update(1L, requestDTO, user);

        assertNotNull(response);
        verify(interviewRepository).save(any(Interview.class));
    }

    @Test
    void update_notFound_throwsException() {
        when(interviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> interviewService.update(999L, requestDTO, user));
    }

    @Test
    void update_unauthorized_throwsException() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        assertThrows(UnauthorizedException.class,
                () -> interviewService.update(1L, requestDTO, otherUser));
    }

    @Test
    void delete_success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        assertDoesNotThrow(() -> interviewService.delete(1L, user));
        assertNotNull(interview, "interview must not be null");
        verify(interviewRepository).delete(interview);
    }

    @Test
    void delete_unauthorized_throwsException() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        assertThrows(UnauthorizedException.class,
                () -> interviewService.delete(1L, otherUser));
        verify(interviewRepository, never()).delete(any(Interview.class));
    }
}