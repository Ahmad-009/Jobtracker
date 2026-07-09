package com.ahmad.jobtracker.dto.request;

import com.ahmad.jobtracker.entity.enums.InterviewType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequestDTO {

    @NotNull(message = "Interview date is required")
    private LocalDateTime interviewDate;

    @NotNull(message = "Interview type is required")
    private InterviewType type;

    private String notes;

    private String outcome;
}