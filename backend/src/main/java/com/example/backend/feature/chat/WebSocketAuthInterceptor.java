package com.example.backend.feature.chat;

import com.example.backend.entity.auth.User;
import com.example.backend.repository.auth.UserRepository;
import com.example.backend.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = extractTokenFromRequest(request);
        if (token == null) {
            log.warn("No token found in WebSocket handshake");
            return false; // Fail handshake if no token
        }

        try {
            // Extract username from token safely
            String username = jwtService.extractUsername(token);
            if (username == null || username.isEmpty()) {
                log.warn("JWT token does not contain a valid username");
                return false;
            }

            // Fetch user from DB
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElse(null);
            if (user == null) {
                log.warn("User not found for username: {}", username);
                return false;
            }

            // Validate token
            if (!jwtService.isTokenValid(token, user)) {
                log.warn("Invalid or expired JWT token for user: {}", username);
                return false;
            }

            // Add userId to WebSocket session attributes
            attributes.put("userId", user.getId());
            log.info("WebSocket authentication successful for user {}", user.getId());
            return true;

        } catch (Exception e) {
            log.error("WebSocket token validation error: {}", e.getMessage());
            return false; // Fail handshake safely
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Optional post-handshake logic
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        // 1. Check query parameters
        String query = request.getURI().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }

        // 2. Check Authorization header
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }

        return null; // No token found
    }
}
