package com.ahmad.jobtracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String linkedinUrl;
    private String notes;
}