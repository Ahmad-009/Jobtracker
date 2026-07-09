package com.ahmad.jobtracker.dto.request;

import com.ahmad.jobtracker.entity.enums.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUpdateDTO {

    private String companyName;
    private String jobTitle;
    private String jobUrl;
    private ApplicationStatus status;
    private Priority priority;
    private JobType jobType;
    private ExperienceLevel experienceRequired;
    private WorkType workType;
    private String domain;
    private Integer salaryMin;
    private Integer salaryMax;
    private LocalDate appliedDate;
    private String notes;
}