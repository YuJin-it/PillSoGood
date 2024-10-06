package com.ssafy.project.domain.notification.repository;

import com.ssafy.project.domain.notification.entity.Notifications;
import com.ssafy.project.domain.userMedication.entity.Status;
import com.ssafy.project.domain.userMedication.entity.UserMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Integer> {
    List<Notifications> findByUserMedication(UserMedication userMedication);

    List<Notifications> findByTimeAndUserMedicationStatusAndEnabled(LocalDateTime time, Status status, boolean enabled);
    List<Notifications> findByUserMedicationAndEnabled(UserMedication userMedication, boolean enabled);

    List<Notifications> findByUserMedicationAndFcmToken(UserMedication userMedication, String fcmToken);
    void deleteByUserMedication(UserMedication userMedication);
}