package com.example.backend.service.admin;

import com.example.backend.dto.admin.request.ApproveAuthorityRequest;
import com.example.backend.dto.admin.request.RejectAuthorityRequest;
import com.example.backend.dto.admin.response.PendingAuthoritiesResponse;

import java.util.List;

public interface AdminService {
    List<PendingAuthoritiesResponse> getPendingAuthorities();
    void approveAuthority(Long userId,ApproveAuthorityRequest request);
    void rejectAuthority(Long userId,RejectAuthorityRequest request);
}
