package com.library.config;

import com.library.book.*;
import com.library.user.*;
import com.library.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository userRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(createUser("Admin", "admin@library.local", "admin123", Role.ADMIN, passwordEncoder));
                userRepository.save(createUser("Management", "manager@library.local", "manager123", Role.MANAGEMENT, passwordEncoder));
                userRepository.save(createUser("Reader", "user@library.local", "user123", Role.USER, passwordEncoder));
            }
            if (bookRepository.count() == 0) {
                Book book = new Book();
                book.setIsbn("9780132350884");
                book.setTitle("Clean Code");
                book.setAuthor("Robert C. Martin");
                book.setCategory("Programming");
                book.setDescription("Software craftsmanship reference book.");
                book.setTotalCopies(5);
                book.setAvailableCopies(5);
                book.setOnlineAvailable(true);
                book.setDigitalContent("Demo digital reading content. Replace this with licensed content for production.");
                bookRepository.save(book);
            }
        };
    }

    private User createUser(String name, String email, String password, Role role, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return user;
    }
}
