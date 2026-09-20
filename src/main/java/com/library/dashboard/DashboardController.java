package com.library.dashboard;

import com.library.book.Book;
import com.library.book.BookRepository;
import com.library.circulation.IssueRepository;
import com.library.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','MANAGEMENT')")
public class DashboardController {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;

    public DashboardController(BookRepository bookRepository, UserRepository userRepository, IssueRepository issueRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
    }

    @GetMapping
    public Map<String, Object> data() {
        return Map.of(
            "users", userRepository.count(),
            "titles", bookRepository.count(),
            "issued", issueRepository.countByStatus("ISSUED"),
            "availableCopies", bookRepository.findAll().stream().mapToInt(Book::getAvailableCopies).sum(),
            "totalCopies", bookRepository.findAll().stream().mapToInt(Book::getTotalCopies).sum()
        );
    }
}
