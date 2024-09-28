package com.ssafy.project.domain.notification.entity;

import com.ssafy.project.domain.userMedication.entity.UserMedication;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "enabled")
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_medicine_id", nullable = false)
    private UserMedication userMedication;

    //알림 수정
    public void updateTime(LocalDateTime newTime) {
        this.time = newTime;
    }

    //알림 상태 수정
    public void updateNotification(boolean enabled) {
        this.enabled = enabled;
    }
}
