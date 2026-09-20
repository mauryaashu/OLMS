package com.library.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> all() {
        return userRepository.findAll();
    }

    public record Create(String name, String email, String phone, String password, Role role) {}

    @PostMapping
    public User create(@RequestBody Create createRequest) {
        User user = new User();
        user.setName(createRequest.name());
        user.setEmail(createRequest.email());
        user.setPhone(createRequest.phone());
        user.setPassword(passwordEncoder.encode(createRequest.password()));
        user.setRole(createRequest.role() == null ? Role.USER : createRequest.role());
        return userRepository.save(user);
    }
}
