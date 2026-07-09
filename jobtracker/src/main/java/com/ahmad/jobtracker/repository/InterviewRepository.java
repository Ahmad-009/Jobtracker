package com.ahmad.jobtracker.repository;

import com.ahmad.jobtracker.entity.Interview;
import com.ahmad.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByJobApplication(JobApplication jobApplication);
    boolean existsByIdAndJobApplication(Long id, JobApplication jobApplication);
}