package com.example.backend.controller.message;

import com.example.backend.dto.message.response.ConversationSummary;
import com.example.backend.dto.message.response.MessageResponse;
import com.example.backend.service.message.MessagingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class MessagingController {

    private final MessagingService messagingService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummary>> getUserConversations(
            @RequestParam @NotNull Long userId) {
        List<ConversationSummary> conversations = messagingService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageResponse>> getConversationMessages(
            @RequestParam @NotNull Long user1Id,
            @RequestParam @NotNull Long user2Id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        List<MessageResponse> messages = messagingService.getConversationMessages(user1Id, user2Id, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, String>> markMessagesAsRead(
            @RequestParam @NotNull Long senderId,
            @RequestParam @NotNull Long receiverId) {
        messagingService.markMessagesAsRead(senderId, receiverId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam @NotNull Long userId) {
        Long count = messagingService.getUnreadMessageCount(null, userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
}
