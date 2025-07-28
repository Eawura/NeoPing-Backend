package com.neoping.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import com.neoping.backend.dto.NotificationDto;
import com.neoping.backend.model.Notification;
import com.neoping.backend.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationDto> getNotificationsForUser(String username) {
        return notificationRepository.findByRecipientOrderByTimeDesc(username)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void markAllAsRead(String username) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByTimeDesc(username);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void markAsRead(Long id, String username) {
        Notification n = notificationRepository.findById(id)
                .filter(notif -> notif.getRecipient().equals(username))
                .orElseThrow();
        n.setRead(true);
        notificationRepository.save(n);
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setUser(n.getUser());
        dto.setAvatar(n.getAvatar());
        dto.setAction(n.getAction());
        dto.setContent(n.getContent());
        dto.setTime(n.getTime());
        dto.setRead(n.isRead());
        dto.setPostId(n.getPostId());
        dto.setCommentId(n.getCommentId());
        dto.setAwardType(n.getAwardType());
        return dto;
    }
}