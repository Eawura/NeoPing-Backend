package com.neoping.backend.service;

import com.neoping.backend.dto.ConversationSummaryDto;
import com.neoping.backend.dto.MessageDto;
import com.neoping.backend.model.Conversation;
import com.neoping.backend.model.Message;
import com.neoping.backend.repository.ConversationRepository;
import com.neoping.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InboxService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public List<ConversationSummaryDto> getInboxForUser(String username) {
        List<Conversation> conversations = conversationRepository.findByUser1OrUser2(username, username);
        return conversations.stream().map(conv -> {
            List<Message> messages = messageRepository.findByConversationIdOrderByTimeAsc(conv.getId());
            Message last = messages.isEmpty() ? null : messages.get(messages.size() - 1);
            boolean unread = messages.stream().anyMatch(m -> m.getRecipient().equals(username) && m.isUnread());
            String otherUser = conv.getUser1().equals(username) ? conv.getUser2() : conv.getUser1();
            String avatar = conv.getUser1().equals(username) ? conv.getAvatar2() : conv.getAvatar1();

            ConversationSummaryDto conversationSummaryDto = new ConversationSummaryDto();
            conversationSummaryDto.setId(conv.getId());
            conversationSummaryDto.setUser(otherUser);
            conversationSummaryDto.setAvatar(avatar);
            conversationSummaryDto.setUnread(unread);
            conversationSummaryDto.setLastMessage(last != null ? last.getContent() : "");
            conversationSummaryDto.setTime(last != null ? last.getTime() : "");
            return conversationSummaryDto;
        }).collect(Collectors.toList());
    }

    public List<MessageDto> getMessagesInConversation(Long conversationId, String username) {
        return messageRepository.findByConversationIdOrderByTimeAsc(conversationId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MessageDto sendMessage(Long conversationId, String sender, MessageDto dto) {
        Conversation conv = conversationRepository.findById(conversationId).orElseThrow();
        String recipient = conv.getUser1().equals(sender) ? conv.getUser2() : conv.getUser1();
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(dto.getContent());
        message.setTime(LocalDateTime.now().toString());
        message.setUnread(true);
        return toDto(messageRepository.save(message));
    }

    public void markConversationAsRead(Long conversationId, String username) {
        List<Message> messages = messageRepository.findByConversationIdOrderByTimeAsc(conversationId);
        messages.stream()
                .filter(m -> m.getRecipient().equals(username))
                .forEach(m -> m.setUnread(false));
        messageRepository.saveAll(messages);
    }

    private MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setSender(m.getSender());
        dto.setRecipient(m.getRecipient());
        dto.setContent(m.getContent());
        dto.setTime(m.getTime());
        dto.setUnread(m.isUnread());
        return dto;
    }
}