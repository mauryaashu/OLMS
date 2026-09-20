package com.library.circulation;

import com.library.book.*;
import com.library.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BigDecimal finePerDay;

    public IssueController(IssueRepository issueRepository, BookRepository bookRepository, UserRepository userRepository, @Value("${app.fine-per-day}") BigDecimal finePerDay) {
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.finePerDay = finePerDay;
    }

    @GetMapping("/me")
    public List<Issue> getMyIssues(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(user -> issueRepository.findByUserIdAndStatus(user.getId(), "ISSUED"))
                .orElse(List.of());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGEMENT')")
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public record IssueRequest(Long userId, String email, Long bookId, int days) {}

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGEMENT')")
    public Issue issueBook(@RequestBody IssueRequest issueRequest) {
        if (issueRequest.days() < 1 || issueRequest.days() > 60) {
            throw new IllegalArgumentException("Days must be between 1 and 60");
        }
        // Check if the user already has the book assigned
        boolean alreadyAssigned = issueRepository.findByUserIdAndStatus(issueRequest.userId(), "ISSUED").stream()
                .anyMatch(issue -> issue.getBook().getId().equals(issueRequest.bookId()));
        if (alreadyAssigned) {
            throw new IllegalStateException("This book is already assigned to the user.");
        }

        Book book = bookRepository.findById(issueRequest.bookId()).orElseThrow();
        User user = userRepository.findById(issueRequest.userId())
                .filter(u -> u.getEmail().equals(issueRequest.email()))
                .orElseThrow(() -> new IllegalArgumentException("User ID and email do not match"));
        if (book.getAvailableCopies() < 1) {
            throw new IllegalStateException("No copy available");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        Issue issue = new Issue();
        issue.setBook(book);
        issue.setUser(user);
        issue.setIssuedAt(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(issueRequest.days()));
        issue.setStatus("ISSUED");
        return issueRepository.save(issue);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGEMENT')")
    public Issue returnBook(@PathVariable Long id) {
        Issue issue = issueRepository.findById(id).orElseThrow();
        if (!"ISSUED".equals(issue.getStatus())) {
            throw new IllegalStateException("Already returned");
        }
        issue.setReturnedAt(LocalDate.now());
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(issue.getDueDate(), issue.getReturnedAt()));
        issue.setFine(finePerDay.multiply(BigDecimal.valueOf(lateDays)));
        issue.setStatus("RETURNED");
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return issueRepository.save(issue);
    }
}
