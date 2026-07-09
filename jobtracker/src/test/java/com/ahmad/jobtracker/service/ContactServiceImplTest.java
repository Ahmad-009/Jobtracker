package com.ahmad.jobtracker.service;

import com.ahmad.jobtracker.dto.request.ContactRequestDTO;
import com.ahmad.jobtracker.dto.response.ContactResponseDTO;
import com.ahmad.jobtracker.entity.Contact;
import com.ahmad.jobtracker.entity.JobApplication;
import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.entity.enums.*;
import com.ahmad.jobtracker.exception.ResourceNotFoundException;
import com.ahmad.jobtracker.exception.UnauthorizedException;
import com.ahmad.jobtracker.repository.ContactRepository;
import com.ahmad.jobtracker.repository.JobApplicationRepository;
import com.ahmad.jobtracker.service.implementation.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private JobApplicationRepository applicationRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private User user;
    private User otherUser;
    private JobApplication application;
    private Contact contact;
    private ContactRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Ahmad").email("ahmad@gmail.com").password("hashed").build();
        otherUser = User.builder().id(2L).name("Ali").email("ali@gmail.com").password("hashed").build();

        application = JobApplication.builder()
                .id(1L).user(user).companyName("Google")
                .jobTitle("Backend Engineer")
                .status(ApplicationStatus.APPLIED)
                .priority(Priority.HIGH)
                .jobType(JobType.FULL_TIME)
                .experienceRequired(ExperienceLevel.FRESH)
                .workType(WorkType.REMOTE)
                .appliedDate(LocalDate.now())
                .build();

        contact = Contact.builder()
                .id(1L).jobApplication(application)
                .name("Sarah").email("sarah@google.com")
                .build();

        requestDTO = new ContactRequestDTO("Sarah", "sarah@google.com", null, null);
    }

    @Test
    void create_success() {
        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
        Contact savedContact = contact;
        assertNotNull(savedContact, "contact must not be null");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        ContactResponseDTO response = contactService.create(1L, requestDTO, user);

        assertNotNull(response);
        assertEquals("Sarah", response.getName());
        verify(contactRepository).save(contactCaptor.capture());
        Contact capturedContact = contactCaptor.getValue();
        assertNotNull(capturedContact, "captured contact");
        assertEquals("Sarah", capturedContact.getName());
    }

    @Test
    void create_applicationNotFound_throwsException() {
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contactService.create(999L, requestDTO, user));
    }

    @Test
    void create_unauthorized_throwsException() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> contactService.create(1L, requestDTO, otherUser));
    }

    @Test
    void getAllByApplication_success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(contactRepository.findByJobApplication(any(JobApplication.class))).thenReturn(List.of(contact));

        List<ContactResponseDTO> result = contactService.getAllByApplication(1L, user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sarah", result.get(0).getName());
    }

    @Test
    void delete_success() {
        Contact existingContact = contact;
        assertNotNull(existingContact, "contact must not be null");
        when(contactRepository.findById(1L)).thenReturn(Optional.of(existingContact));

        assertDoesNotThrow(() -> contactService.delete(1L, user));
        verify(contactRepository).delete(existingContact);
    }

    @Test
    void delete_unauthorized_throwsException() {
        Contact existingContact = contact;
        assertNotNull(existingContact, "contact must not be null");
        when(contactRepository.findById(1L)).thenReturn(Optional.of(existingContact));

        assertThrows(UnauthorizedException.class,
                () -> contactService.delete(1L, otherUser));
        verify(contactRepository, never()).delete(existingContact);
    }
}