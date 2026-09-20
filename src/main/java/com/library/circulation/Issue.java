package com.library.circulation;

import com.library.book.Book;
import com.library.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.*;
import java.time.*;

@Entity
@Table(name = "issues")
@Getter
@Setter
@NoArgsConstructor
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private User user;

    private LocalDate issuedAt;

    private LocalDate dueDate;

    private LocalDate returnedAt;

    private BigDecimal fine = BigDecimal.ZERO;

    private String status;
}
