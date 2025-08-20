package com.example.backend.dto.admin.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectAuthorityRequest {
    private Long authorityId;
    private String email;
    private String fullName;
    private String reason;
}

