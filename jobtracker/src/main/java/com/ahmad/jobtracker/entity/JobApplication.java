package com.ahmad.jobtracker.entity;

import com.ahmad.jobtracker.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobTitle;

    @Column
    private String jobUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceRequired;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @Column
    private String domain;

    @Column
    private Integer salaryMin;

    @Column
    private Integer salaryMax;

    @Column(nullable = false)
    private LocalDate appliedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL)
    private List<Interview> interviews;

    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL)
    private List<Contact> contacts;

    @CreationTimestamp
    private LocalDateTime createdAt;
}