package com.library.book;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> all(@RequestParam(defaultValue = "") String query) {
        return query.isBlank() ? bookRepository.findAll() : bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    @GetMapping("/{id}")
    public Book one(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    @GetMapping("/public/online")
    public List<Book> online(@RequestParam(defaultValue = "") String query) {
        return query.isBlank() ? bookRepository.findAll().stream().filter(Book::isOnlineAvailable).toList() : bookRepository.searchOnline(query);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Book create(@RequestBody Book book) {
        book.setId(null);
        book.setAvailableCopies(book.getTotalCopies());
        return bookRepository.save(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Book update(@PathVariable Long id, @RequestBody Book book) {
        Book existingBook = bookRepository.findById(id).orElseThrow();
        existingBook.setIsbn(book.getIsbn());
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setCategory(book.getCategory());
        existingBook.setDescription(book.getDescription());
        existingBook.setOnlineAvailable(book.isOnlineAvailable());
        existingBook.setDigitalContent(book.getDigitalContent());
        int diff = book.getTotalCopies() - existingBook.getTotalCopies();
        existingBook.setTotalCopies(book.getTotalCopies());
        existingBook.setAvailableCopies(existingBook.getAvailableCopies() + diff);
        return bookRepository.save(existingBook);
    }
}
