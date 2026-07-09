package com.ahmad.jobtracker.service.implementation;

import com.ahmad.jobtracker.dto.response.StatsDTO;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.entity.enums.ApplicationStatus;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Override
    public StatsDTO getStats(User user) {
        log.info("Fetching application statistics for user id: {}", user.getId());

        long total = applicationRepository.countByUser(user);

        Map<String, Long> byStatus = new HashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            long count = applicationRepository.countByUserAndStatus(user, status);
            byStatus.put(status.name(), count);
        }

        long responded = byStatus.getOrDefault("INTERVIEWING", 0L)
                + byStatus.getOrDefault("OFFERED", 0L)
                + byStatus.getOrDefault("REJECTED", 0L);

        double responseRate = total > 0
                ? Math.round((responded * 100.0 / total) * 10.0) / 10.0
                : 0.0;

        LocalDate now = LocalDate.now();

        long thisMonth = applicationRepository
                .countByUserAndAppliedDateBetween(
                        user,
                        now.withDayOfMonth(1),
                        now);

        long thisWeek = applicationRepository
                .countByUserAndAppliedDateBetween(
                        user,
                        now.minusDays(7),
                        now);

        log.info("Statistics generated successfully for user id: {}", user.getId());

        return StatsDTO.builder()
                .totalApplications(total)
                .byStatus(byStatus)
                .responseRate(responseRate)
                .thisMonth(thisMonth)
                .thisWeek(thisWeek)
                .build();
    }
}