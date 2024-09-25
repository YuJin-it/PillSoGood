package com.ssafy.project.domain.notification.repository;

import com.ssafy.project.domain.notification.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Integer> {
}
