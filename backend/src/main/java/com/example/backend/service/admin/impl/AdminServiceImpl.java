package com.example.backend.service.admin.impl;

import com.example.backend.dto.admin.request.ApproveAuthorityRequest;
import com.example.backend.dto.admin.request.RejectAuthorityRequest;
import com.example.backend.dto.admin.response.PendingAuthoritiesResponse;
import com.example.backend.entity.auth.RoleCode;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.auth.UserStatus;
import com.example.backend.entity.profile.AuthorityProfile;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.auth.UserRepository;
import com.example.backend.repository.profile.AuthorityProfileRepository;
import com.example.backend.service.EmailService;
import com.example.backend.service.admin.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AuthorityProfileRepository authorityProfileRepository;
    private final EmailService emailService;

    @Override
    public List<PendingAuthoritiesResponse> getPendingAuthorities() {
        List<User> pendingAuthorities = userRepository.findAllByRoles_CodeAndStatus(RoleCode.AUTHORITY, UserStatus.PENDING);

        return pendingAuthorities.stream()
                .map(user -> {
                    Optional<AuthorityProfile> profileOpt = authorityProfileRepository.findById(user.getId());
                    if (profileOpt.isEmpty()) return null;
                    AuthorityProfile profile = profileOpt.get();
                    return PendingAuthoritiesResponse.builder()
                            .userId(user.getId())
                            .fullName(profile.getFullName())
                            .designation(profile.getDesignation())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .employeeId(profile.getEmployeeId())
                            .employeeIdImageUrl(profile.getEmployeeIdImageUrl())
                            .officeDivision(profile.getOfficeDivision())
                            .officeDistrict(profile.getOfficeDistrict())
                            .officeUpazila(profile.getOfficeUpazila())
                            .officeUnion(profile.getOfficeUnion())
                            .address(profile.getAddress())
                            .build();
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void approveAuthority(Long userId, ApproveAuthorityRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authority not found"));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        emailService.sendAcceptationEmail(request.getEmail(), request.getFullName());
    }

    @Override
    @Transactional
    public void rejectAuthority(Long userId, RejectAuthorityRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authority not found"));

        // First delete OTP codes
        userRepository.deleteOtpCodesForUser(userId);

        // Then delete the authority profile
        authorityProfileRepository.deleteById(userId);

        // Then delete the user
        userRepository.delete(user);

        // Send rejection email
        emailService.sendRejectionEmail(request.getEmail(), request.getFullName(), request.getReason());
    }
}
