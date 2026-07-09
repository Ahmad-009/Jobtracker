package com.ahmad.jobtracker.controller;

import com.ahmad.jobtracker.dto.request.ApplicationRequestDTO;
import com.ahmad.jobtracker.dto.response.ApplicationSummaryDTO;
import com.ahmad.jobtracker.entity.enums.*;
import com.ahmad.jobtracker.service.JobApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JobApplicationService applicationService;

    private ApplicationSummaryDTO summaryDTO() {
        return ApplicationSummaryDTO.builder()
                .id(1L)
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
    }

    @Test
    @WithMockUser
    void getAll_returns200() throws Exception {
        List<ApplicationSummaryDTO> content = Objects.requireNonNull(List.of(summaryDTO()), "content must not be null");
        Page<ApplicationSummaryDTO> page = new PageImpl<>(content);
        when(applicationService.getAll(any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("Google"));
    }

    @Test
    @WithMockUser
    void create_success_returns201() throws Exception {
        ApplicationRequestDTO request = ApplicationRequestDTO.builder()
                .companyName("Google")
                .jobTitle("Backend Engineer")
                .status(ApplicationStatus.APPLIED)
                .priority(Priority.HIGH)
                .jobType(JobType.FULL_TIME)
                .experienceRequired(ExperienceLevel.FRESH)
                .workType(WorkType.REMOTE)
                .appliedDate(LocalDate.now())
                .build();

        when(applicationService.create(any(ApplicationRequestDTO.class), any())).thenReturn(summaryDTO());

        MediaType jsonMediaType = Objects.requireNonNull(MediaType.APPLICATION_JSON);
        String requestBody = Objects.requireNonNull(objectMapper.writeValueAsString(request));

        mockMvc.perform(post("/applications")
                .contentType(jsonMediaType)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Google"));
    }
}
