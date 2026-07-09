package com.ahmad.jobtracker.repository;

import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Page<JobApplication> findByUser(User user, Pageable pageable);
    long countByUserAndStatus(User user, ApplicationStatus status);
    boolean existsByIdAndUser(Long id, User user);
    long countByUser(User user);
    long countByUserAndAppliedDateBetween(User user, LocalDate start, LocalDate end);
}