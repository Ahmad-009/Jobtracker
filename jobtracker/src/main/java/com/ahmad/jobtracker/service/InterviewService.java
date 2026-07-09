package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.InterviewRequestDTO;
import com.ahmad.jobtracker.dto.response.InterviewResponseDTO;
import com.ahmad.jobtracker.entity.User;
import java.util.List;

public interface InterviewService {
    InterviewResponseDTO create(Long applicationId, InterviewRequestDTO dto, User user);
    List<InterviewResponseDTO> getAllByApplication(Long applicationId, User user);
    InterviewResponseDTO update(Long id, InterviewRequestDTO dto, User user);
    void delete(Long id, User user);
}