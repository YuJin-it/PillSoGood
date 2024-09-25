package com.ssafy.project.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    @Autowired
    private Scheduler scheduler;

    // Job을 동적으로 예약하는 메소드
    public void scheduleMedicationReminder(String medicationName, LocalDateTime alertTime) throws SchedulerException {
        // JobDetail 생성
        JobDetail jobDetail = JobBuilder.newJob(ReminderJob.class)
                .withIdentity("medicationReminderJob", "group1")
                .usingJobData("medicationName", medicationName)
                .build();

        // Trigger 생성 - 알림 시간이 되면 Job을 트리거
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("medicationReminderTrigger", "group1")
                .startAt(Date.from(alertTime.atZone(ZoneId.systemDefault()).toInstant())) // 알림 시간 설정
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow()) // 미처리된 트리거 처리 방법
                .build();

        // 스케줄러에 JobDetail과 Trigger를 등록
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
