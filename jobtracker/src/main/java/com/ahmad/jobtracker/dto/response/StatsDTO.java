package com.ahmad.jobtracker.dto.response;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDTO {
    private long totalApplications;
    private Map<String, Long> byStatus;
    private long thisMonth;
    private long thisWeek;
    private double responseRate;
}