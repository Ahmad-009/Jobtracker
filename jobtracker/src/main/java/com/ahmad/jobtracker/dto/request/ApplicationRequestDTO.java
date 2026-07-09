package com.ahmad.jobtracker.dto.request;

import com.ahmad.jobtracker.entity.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequestDTO {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String jobUrl;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceRequired;

    @NotNull(message = "Work type is required")
    private WorkType workType;

    private String domain;

    private Integer salaryMin;

    private Integer salaryMax;

    @NotNull(message = "Applied date is required")
    private LocalDate appliedDate;

    private String notes;
}