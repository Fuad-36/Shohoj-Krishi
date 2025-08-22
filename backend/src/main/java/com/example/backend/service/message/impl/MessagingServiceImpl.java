package com.example.backend.service.message.impl;



import com.example.backend.dto.message.response.ConversationSummary;
import com.example.backend.dto.message.response.MessageResponse;
import com.example.backend.entity.message.Conversation;
import com.example.backend.entity.message.Message;
import com.example.backend.entity.message.MessageType;
import com.example.backend.repository.message.ConversationRepository;
import com.example.backend.repository.message.MessageRepository;
import com.example.backend.service.auth.UserService;
import com.example.backend.service.message.MessagingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserService userService; // Your existing user service

    @Override
    public MessageResponse sendMessage(Long senderId, Long receiverId, String content, MessageType messageType) {
        // Create or get conversation
        Conversation conversation = getOrCreateConversation(senderId, receiverId);

        // Create and save message
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .messageType(messageType)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);

        // Update conversation's last message time
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        // Get sender name for response
        String senderName = userService.getUserName(senderId);

        return new MessageResponse(savedMessage, senderName);
    }

    @Override
    public List<MessageResponse> getConversationMessages(Long user1Id, Long user2Id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Message> messages = messageRepository.findConversationMessagesPageable(user1Id, user2Id, pageable);

        return messages.getContent().stream()
                .map(message -> {
                    String senderName = userService.getUserName(message.getSenderId());
                    return new MessageResponse(message, senderName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationSummary> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findUserConversations(userId);

        return conversations.stream()
                .map(conversation -> {
                    Long otherUserId = conversation.getUser1Id().equals(userId)
                            ? conversation.getUser2Id()
                            : conversation.getUser1Id();

                    String otherUserName = userService.getUserName(otherUserId);
                    Long unreadCount = getUnreadMessageCount(otherUserId, userId);

                    return ConversationSummary.builder()
                            .conversationId(conversation.getId())
                            .otherUserId(otherUserId)
                            .otherUserName(otherUserName)
                            .lastMessageAt(conversation.getLastMessageAt())
                            .unreadCount(unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(Long senderId, Long receiverId) {
        messageRepository.markMessagesAsRead(senderId, receiverId);
    }

    @Override
    public Long getUnreadMessageCount(Long senderId, Long receiverId) {
        return messageRepository.countUnreadMessages(receiverId);
    }

    @Override
    public List<Long> getUserContacts(Long userId) {
        List<Conversation> conversations = conversationRepository.findUserConversations(userId);

        return conversations.stream()
                .map(conversation ->
                        conversation.getUser1Id().equals(userId)
                                ? conversation.getUser2Id()
                                : conversation.getUser1Id())
                .collect(Collectors.toList());
    }

    private Conversation getOrCreateConversation(Long user1Id, Long user2Id) {
        Optional<Conversation> existingConversation = conversationRepository.findByUsers(user1Id, user2Id);

        if (existingConversation.isPresent()) {
            return existingConversation.get();
        } else {
            Conversation newConversation = Conversation.builder()
                    .user1Id(user1Id)
                    .user2Id(user2Id)
                    .createdAt(LocalDateTime.now())
                    .lastMessageAt(LocalDateTime.now())
                    .build();
            return conversationRepository.save(newConversation);
        }
    }
}

