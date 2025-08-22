package com.example.backend.service.auth;

import com.example.backend.entity.auth.User;
import com.example.backend.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getEmail) // or maybe User::getPhone or a future "displayName" field
                .orElse("Unknown User");
    }
}
