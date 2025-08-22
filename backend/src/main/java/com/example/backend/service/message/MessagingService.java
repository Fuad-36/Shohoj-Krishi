package com.example.backend.service.message;



import com.example.backend.dto.message.response.ConversationSummary;
import com.example.backend.dto.message.response.MessageResponse;
import com.example.backend.entity.message.MessageType;

import java.util.List;

public interface MessagingService {

    MessageResponse sendMessage(Long senderId, Long receiverId, String content, MessageType messageType);

    List<MessageResponse> getConversationMessages(Long user1Id, Long user2Id, int page, int size);

    List<ConversationSummary> getUserConversations(Long userId);

    void markMessagesAsRead(Long senderId, Long receiverId);

    Long getUnreadMessageCount(Long senderId, Long receiverId);

    List<Long> getUserContacts(Long userId);
}

