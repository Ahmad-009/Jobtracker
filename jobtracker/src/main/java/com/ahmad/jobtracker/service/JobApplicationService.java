package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.ApplicationRequestDTO;
import com.ahmad.jobtracker.dto.request.ApplicationUpdateDTO;
import com.ahmad.jobtracker.dto.response.ApplicationDetailDTO;
import com.ahmad.jobtracker.dto.response.ApplicationSummaryDTO;
import com.ahmad.jobtracker.entity.User;
import org.springframework.data.domain.Page;

public interface JobApplicationService {
    ApplicationSummaryDTO create(ApplicationRequestDTO dto, User user);
    Page<ApplicationSummaryDTO> getAll(User user, int page, int size);
    ApplicationDetailDTO getById(Long id, User user);
    ApplicationSummaryDTO update(Long id, ApplicationUpdateDTO dto, User user);
    void delete(Long id, User user);
}