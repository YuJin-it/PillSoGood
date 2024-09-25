package com.ssafy.project.domain.notification.controller;

import com.ssafy.project.domain.notification.dto.MedicationNotificationResponseDTO;
import com.ssafy.project.domain.notification.dto.NotificationRequestDTO;
import com.ssafy.project.domain.notification.dto.NotificationResponseDTO;
import com.ssafy.project.domain.notification.service.FCMService;
import com.ssafy.project.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/remainder")
public class NotificationController {

    private final NotificationService notificationService;
    private final FCMService fcmService;

    // 복약 알림 정보 조회
    @GetMapping("/{medicationId}")
    public ResponseEntity<MedicationNotificationResponseDTO> getMedicationNotifications(@PathVariable int medicationId) {
        MedicationNotificationResponseDTO response = notificationService.getMedicationNotifications(medicationId);
        return ResponseEntity.ok(response);
    }

    // 알림 등록
    @PostMapping("/register")
    public ResponseEntity<String> registerNotification(@RequestBody NotificationRequestDTO notificationRequestDTO) {
        notificationService.registerNotification(notificationRequestDTO);
        return ResponseEntity.ok("알림이 성공적으로 등록되었습니다.");
    }

    // 알림 트리거
//    @PostMapping("/trigger")
//    public ResponseEntity<NotificationResponseDTO> sendNotification(@RequestBody NotificationRequestDTO notificationRequestDTO) {
//        NotificationResponseDTO response = fcmService.sendNotification(notificationRequestDTO);
//        return ResponseEntity.ok(response);
//    }
}
