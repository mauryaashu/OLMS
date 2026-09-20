package com.library.book;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "online_available")
    private boolean onlineAvailable;

    @Column(name = "digital_content", columnDefinition = "TEXT")
    private String digitalContent;

    @Column(name = "total_copies")
    private int totalCopies;

    @Column(name = "available_copies")
    private int availableCopies;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
