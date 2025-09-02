// package com.example.backend.controller.auth;

// import com.example.backend.dto.auth.request.LoginRequest;
// import com.example.backend.dto.auth.response.AuthResponse;
// import com.example.backend.service.auth.AuthService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// public class AuthenticationController {

//     private final AuthService authService;

//     @PostMapping("/login")
//     public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//         return ResponseEntity.ok(authService.authenticate(request));
//     }
// }
