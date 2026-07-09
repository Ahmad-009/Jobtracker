package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.ContactRequestDTO;
import com.ahmad.jobtracker.dto.response.ContactResponseDTO;
import com.ahmad.jobtracker.entity.User;
import java.util.List;

public interface ContactService {
    ContactResponseDTO create(Long applicationId, ContactRequestDTO dto, User user);
    List<ContactResponseDTO> getAllByApplication(Long applicationId, User user);
    ContactResponseDTO update(Long id, ContactRequestDTO dto, User user);
    void delete(Long id, User user);
}