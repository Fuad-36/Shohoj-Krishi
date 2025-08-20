package com.example.backend.controller.admin;


import com.example.backend.dto.admin.request.ApproveAuthorityRequest;
import com.example.backend.dto.admin.request.RejectAuthorityRequest;
import com.example.backend.dto.admin.response.PendingAuthoritiesResponse;
import com.example.backend.dto.auth.request.VerifyOtpRequest;
import com.example.backend.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthorityApprovalController {

    private final AdminService adminAuthorityApprovalService;

    @GetMapping("/pending-authorities")
    public ResponseEntity<List<PendingAuthoritiesResponse>> getPendingAuthorities() {
        List<PendingAuthoritiesResponse> pendingList = adminAuthorityApprovalService.getPendingAuthorities();
        return ResponseEntity.ok(pendingList);
    }

    @PostMapping("/approve-authority/{userId}")
    public ResponseEntity<String> approveAuthority(@Valid @RequestBody ApproveAuthorityRequest request, @PathVariable Long userId) {
        adminAuthorityApprovalService.approveAuthority(userId,request);
        return ResponseEntity.ok("Authority approved successfully");
    }

    @PostMapping("/reject-authority/{userId}")
    public ResponseEntity<String> rejectAuthority(@Valid @RequestBody RejectAuthorityRequest request, @PathVariable Long userId) {
        adminAuthorityApprovalService.rejectAuthority(userId, request);
        return ResponseEntity.ok("Authority rejected successfully");
    }
}
