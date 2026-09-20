package com.library.notification;

import com.library.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String channel;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
