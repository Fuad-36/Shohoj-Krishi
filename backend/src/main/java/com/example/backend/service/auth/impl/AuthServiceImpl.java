package com.example.backend.service.auth.impl;

import com.example.backend.config.AppConfig;
import com.example.backend.dto.auth.request.RegisterRequest;
import com.example.backend.dto.auth.request.VerifyOtpRequest;
import com.example.backend.dto.auth.response.MessageResponse;
import com.example.backend.dto.auth.response.RegisterResponse;
import com.example.backend.entity.auth.*;
import com.example.backend.entity.profile.*;
import com.example.backend.exception.*;
import com.example.backend.repository.auth.*;
import com.example.backend.repository.profile.*;
import com.example.backend.service.EmailService;
import com.example.backend.service.auth.AuthService;
import com.example.backend.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final AuthorityProfileRepository authorityProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AppConfig appConfig;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already registered");
        }

        // Create user
        User user = createUser(request);

        // Create profile based on role
        createProfile(user, request);

        // Generate and save OTP
        String otp = OtpUtil.generateOtp(appConfig.getOtpLength());
        saveOtp(user, otp, request.getEmail());

        // Send OTP email
        emailService.sendOtpEmail(request.getEmail(), otp);

        return RegisterResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .message("Registration successful. Please check your email for OTP.")
                .otpSent(true)
                .build();
    }

    private User createUser(RegisterRequest request) {
        // Get role
        Role role = roleRepository.findByCode(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Validate password requirement
        boolean isAuthority = request.getRole() == RoleCode.AUTHORITY;
        if (!isAuthority && (request.getPassword() == null || request.getPassword().isBlank())) {
            throw new InvalidOtpException("Password is required for Farmers and Buyers");
        }

        // Set password - real password for Farmer/Buyer, temp for Authority
        String passwordHash = isAuthority ?
                passwordEncoder.encode("TEMP_" + System.currentTimeMillis()) :
                passwordEncoder.encode(request.getPassword());

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordHash)
                .roles(Set.of(role))
                .status(UserStatus.PENDING)
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(false)
                .build();

        return userRepository.save(user);
    }

    private void createProfile(User user, RegisterRequest request) {
        switch (request.getRole()) {
            case FARMER -> {
                FarmerProfile profile = FarmerProfile.builder()
                        .user(user)
                        .fullName(request.getFullName())
                        .nidNumber(request.getNidNumber())
                        .division(request.getDivision())
                        .district(request.getDistrict())
                        .upazila(request.getUpazila())
                        .unionPorishod(request.getUnion())
                        .address(request.getAddress())
                        .farmSizeAc(request.getFarmSizeAc())
                        .farmType(request.getFarmType())
                        .build();
                farmerProfileRepository.save(profile);
            }
            case BUYER -> {
                BuyerProfile profile = BuyerProfile.builder()
                        .user(user)
                        .fullName(request.getFullName())
                        .organisation(request.getOrganisation())
                        .nidNumber(request.getNidNumber())
                        .division(request.getDivision())
                        .district(request.getDistrict())
                        .upazila(request.getUpazila())
                        .unionPorishod(request.getUnion())
                        .address(request.getAddress())
                        .build();
                buyerProfileRepository.save(profile);
            }
            case AUTHORITY -> {
                AuthorityProfile profile = AuthorityProfile.builder()
                        .user(user)
                        .fullName(request.getFullName())
                        .designation(request.getDesignation())
                        .employeeId(request.getEmployeeId())
                        .employeeIdImageUrl(request.getEmployeeIdImageUrl())
                        .officeDivision(request.getOfficeDivision())
                        .officeDistrict(request.getOfficeDistrict())
                        .officeUpazila(request.getOfficeUpazila())
                        .officeUnion(request.getOfficeUnion())
                        .address(request.getAddress())
                        .build();
                authorityProfileRepository.save(profile);
            }
        }
    }

    private void saveOtp(User user, String otp, String email) {
        OtpCode otpCode = OtpCode.builder()
                .user(user)
                .targetType(OtpCode.TargetType.EMAIL)
                .targetValue(email)
                .purpose(OtpCode.Purpose.SIGNUP)
                .codeHash(passwordEncoder.encode(otp))
                .expiresAt(Instant.now().plusSeconds(appConfig.getOtpExpirationSeconds()))
                .used(false)
                .attempts((short) 0)
                .build();

        otpCodeRepository.save(otpCode);
    }

    @Override
    public MessageResponse verifyOtp(VerifyOtpRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Find valid OTP
        OtpCode otpCode = otpCodeRepository
                .findTopByUserAndTargetTypeAndTargetValueAndPurposeAndUsedFalseAndExpiresAtAfter(
                        user,
                        OtpCode.TargetType.EMAIL,
                        request.getEmail(),
                        OtpCode.Purpose.SIGNUP,
                        Instant.now()
                )
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired OTP"));

        // Check OTP attempts
        if (otpCode.getAttempts() >= 3) {
            throw new InvalidOtpException("Too many failed attempts. Please request a new OTP.");
        }

        // Verify OTP
        if (!passwordEncoder.matches(request.getOtp(), otpCode.getCodeHash())) {
            // Increment attempts
            otpCode.setAttempts((short) (otpCode.getAttempts() + 1));
            otpCodeRepository.save(otpCode);
            throw new InvalidOtpException("Invalid OTP");
        }

        // Mark OTP as used
        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        // Update user email verification status
        user.setEmailVerified(true);
        // Check user role
        boolean isAuthority = user.getRoles().stream()
                .anyMatch(role -> role.getCode() == RoleCode.AUTHORITY);

        String message;
        if (isAuthority) {
            // Authority needs admin approval
            message = "Email verified successfully. Please wait for admin approval.";
        } else {
            // Farmers and Buyers are ready to login
            user.setStatus(UserStatus.ACTIVE);
            user.setIsActive(true);
            message = "Email verified successfully. You can now login with your email and password.";
        }

        userRepository.save(user);

        return new MessageResponse(message, true);
    }

    @Override
    public MessageResponse resendOtp(String email) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if email is already verified
        if (user.getEmailVerified()) {
            throw new InvalidOtpException("Email already verified");
        }

        // Check for recent OTP
        otpCodeRepository
                .findTopByUserAndTargetTypeAndTargetValueAndPurposeAndUsedFalseAndExpiresAtAfter(
                        user,
                        OtpCode.TargetType.EMAIL,
                        email,
                        OtpCode.Purpose.SIGNUP,
                        Instant.now()
                )
                .ifPresent(existingOtp -> {
                    // If OTP was created less than 1 minute ago, don't allow resend
                    if (existingOtp.getCreatedAt().plusSeconds(60).isAfter(Instant.now())) {
                        throw new InvalidOtpException("Please wait 1 minute before requesting a new OTP");
                    }
                    // Mark existing OTP as used
                    existingOtp.setUsed(true);
                    otpCodeRepository.save(existingOtp);
                });

        // Generate new OTP
        String otp = OtpUtil.generateOtp(appConfig.getOtpLength());
        saveOtp(user, otp, email);

        // Send OTP email
        emailService.sendOtpEmail(email, otp);

        return new MessageResponse("OTP sent successfully", true);
    }
}