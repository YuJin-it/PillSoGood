package com.ssafy.project.domain.notification.controller;

import com.ssafy.project.domain.notification.dto.NotificationRequestDTO;
import com.ssafy.project.domain.notification.dto.NotificationResponseDTO;
import com.ssafy.project.domain.notification.service.FCMService;
import com.ssafy.project.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/remainder")
public class NotificationController {

    private final NotificationService notificationService;
    private final FCMService fcmService;

    @PostMapping("/trigger")
    public ResponseEntity<NotificationResponseDTO> sendNotification(@RequestBody NotificationRequestDTO notificationRequestDTO) {
        NotificationResponseDTO response = fcmService.sendNotification(notificationRequestDTO);
        return ResponseEntity.ok(response);
    }
}
