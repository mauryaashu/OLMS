package com.library.ai;

import com.library.book.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final BookRepository bookRepository;
    private final String aiProvider;

    public AiController(BookRepository bookRepository, @Value("${app.ai.provider}") String aiProvider) {
        this.bookRepository = bookRepository;
        this.aiProvider = aiProvider;
    }

    public record Ask(String question) {}

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody Ask askRequest) {
        String question = askRequest.question().toLowerCase();
        List<Book> matches = bookRepository.findAll().stream()
                .filter(book -> question.contains(book.getTitle().toLowerCase()) || question.contains(book.getAuthor().toLowerCase()))
                .toList();
        String answer;
        if (!matches.isEmpty()) {
            Book book = matches.get(0);
            answer = "Book: " + book.getTitle() + " by " + book.getAuthor() + ". Physical availability: " + book.getAvailableCopies() + "/" + book.getTotalCopies() + " copies available. Online reading: " + (book.isOnlineAvailable() ? "available" : "not available") + ". AI provider is currently configured as '" + aiProvider + "'.";
        } else {
            answer = "I could not find a matching book in the library catalog. Try the exact title, ISBN, or author name.";
        }
        return Map.of("answer", answer, "provider", aiProvider);
    }

    @GetMapping("/book/{id}/read")
    public Map<String, Object> read(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (!book.isOnlineAvailable()) {
            throw new IllegalStateException("Online reading is not enabled for this book");
        }
        return Map.of("title", book.getTitle(), "content", Optional.ofNullable(book.getDigitalContent()).orElse("No digital content uploaded yet."));
    }
}
