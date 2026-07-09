package com.ahmad.jobtracker.dto.response;

import com.ahmad.jobtracker.entity.enums.InterviewType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponseDTO {
    private Long id;
    private LocalDateTime interviewDate;
    private InterviewType type;
    private String notes;
    private String outcome;
}