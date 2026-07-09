package com.ahmad.jobtracker.config;

import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.entity.enums.ApplicationStatus;
import com.ahmad.jobtracker.entity.enums.ExperienceLevel;
import com.ahmad.jobtracker.entity.enums.JobType;
import com.ahmad.jobtracker.entity.enums.Priority;
import com.ahmad.jobtracker.entity.enums.WorkType;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User demoUser = userRepository.findByEmail("demo@example.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Demo User")
                        .email("demo@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .build()));

        if (jobApplicationRepository.countByUser(demoUser) > 0) {
            return;
        }

        jobApplicationRepository.save(JobApplication.builder()
                .user(demoUser)
                .companyName("Acme Corp")
                .jobTitle("Backend Engineer")
                .jobUrl("https://example.com/jobs/1")
                .status(ApplicationStatus.INTERVIEWING)
                .priority(Priority.HIGH)
                .jobType(JobType.FULL_TIME)
                .experienceRequired(ExperienceLevel.TWO_PLUS)
                .workType(WorkType.REMOTE)
                .domain("Fintech")
                .salaryMin(9000)
                .salaryMax(12000)
                .appliedDate(LocalDate.now().minusDays(3))
                .notes("Great fit, second interview scheduled")
                .build());

        jobApplicationRepository.save(JobApplication.builder()
                .user(demoUser)
                .companyName("Globex")
                .jobTitle("Java Developer")
                .jobUrl("https://example.com/jobs/2")
                .status(ApplicationStatus.APPLIED)
                .priority(Priority.MEDIUM)
                .jobType(JobType.CONTRACT)
                .experienceRequired(ExperienceLevel.ONE_PLUS)
                .workType(WorkType.HYBRID)
                .domain("E-commerce")
                .salaryMin(7000)
                .salaryMax(9500)
                .appliedDate(LocalDate.now().minusDays(7))
                .notes("Waiting for recruiter response")
                .build());
    }
}
