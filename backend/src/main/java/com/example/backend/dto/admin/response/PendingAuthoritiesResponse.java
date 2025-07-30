package com.example.backend.dto.admin.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingAuthoritiesResponse {
    private Long userId;
    private String fullName;
    private String designation;
    private String email;
    private String phone;
    private String employeeId;
    private String employeeIdImageUrl;
    private String officeDivision;
    private String officeDistrict;
    private String officeUpazila;
    private String officeUnion;
    private String address;
}
