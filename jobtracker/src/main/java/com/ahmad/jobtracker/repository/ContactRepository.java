package com.ahmad.jobtracker.repository;

import com.ahmad.jobtracker.entity.Contact;
import com.ahmad.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByJobApplication(JobApplication jobApplication);
    boolean existsByIdAndJobApplication(Long id, JobApplication jobApplication);
}