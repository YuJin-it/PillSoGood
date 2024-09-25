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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_medicine_id", nullable = false)
    private UserMedication userMedication;

    //알림 메시지
    public String getNotificationMessage() {
        return String.format("%s 복용 시간이 되어 %s 약물을 드실 시간입니다.",
                this.time.toLocalTime(), this.userMedication.getName());
    }
}
