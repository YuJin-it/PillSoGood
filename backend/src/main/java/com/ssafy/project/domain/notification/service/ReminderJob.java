package com.ssafy.project.domain.notification.service;

import com.ssafy.project.domain.notification.dto.NotificationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderJob implements Job {

    private final FCMService fcmService;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        NotificationRequestDTO request = (NotificationRequestDTO) context.getJobDetail().getJobDataMap().get("notificationRequestDTO");
//        fcmService.sendNotification(request);
    }
}
