package com.example.backend.dto.admin.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveAuthorityRequest {
    private Long authorityId;
    private String email;
    private String fullName;

}