package com.library.notification;

import com.library.circulation.*;
import com.library.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final IssueRepository issueRepository;
    private final int reminderDays;

    public NotificationService(NotificationRepository notificationRepository, IssueRepository issueRepository, @Value("${app.reminder-days-before}") int reminderDays) {
        this.notificationRepository = notificationRepository;
        this.issueRepository = issueRepository;
        this.reminderDays = reminderDays;
    }

    public int run() {
        LocalDate targetDate = LocalDate.now().plusDays(reminderDays);
        int notificationCount = 0;
        for (Issue issue : issueRepository.findByDueDateAndStatus(targetDate, "ISSUED")) {
            User user = issue.getUser();
            String message = "Library reminder: '" + issue.getBook().getTitle() + "' is due on " + issue.getDueDate() + ". Please return it on time to avoid fine.";
            saveNotification(user, "EMAIL", "Book due reminder", message);
            saveNotification(user, "SMS", null, message);
            notificationCount += 2;
        }
        return notificationCount;
    }

    private void saveNotification(User user, String channel, String subject, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setChannel(channel);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setStatus("LOGGED");
        notificationRepository.save(notification);
        System.out.println("[" + channel + "] " + user.getEmail() + " -> " + message);
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 9 * * *")
    public void scheduled() {
        run();
    }
}
