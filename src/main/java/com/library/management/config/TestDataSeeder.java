package com.library.management.config;

import com.library.management.entity.Book;
import com.library.management.entity.Role;
import com.library.management.entity.User;
import com.library.management.enums.BookStatus;
import com.library.management.enums.RoleName;
import com.library.management.repository.BookRepository;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedBooks();
    }

    private void seedUsers() {
        if (userRepository.existsByEmail("admin@library.com")) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
        Role userRole = roleRepository.findByName(RoleName.USER).orElseThrow();

        User admin = User.builder()
                .fullName("System Administrator")
                .email("admin@library.com")
                .password(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .isActive(true)
                .build();

        User user = User.builder()
                .fullName("Test User")
                .email("user@library.com")
                .password(passwordEncoder.encode("user123"))
                .role(userRole)
                .isActive(true)
                .build();

        userRepository.save(admin);
        userRepository.save(user);
    }

    private void seedBooks() {
        if (bookRepository.count() > 0) {
            return;
        }

        User admin = userRepository.findByEmail("admin@library.com").orElseThrow();

        bookRepository.save(Book.builder()
                .title("Clean Code")
                .authorSurname("Martin")
                .authorInitials("R.C.")
                .publicationYear(2008)
                .copiesCount(4)
                .genre("Programming")
                .language("English")
                .isbn("9780132350884")
                .publisher("Prentice Hall")
                .description("A handbook of agile software craftsmanship.")
                .status(BookStatus.AVAILABLE)
                .createdBy(admin)
                .updatedBy(admin)
                .build());

        bookRepository.save(Book.builder()
                .title("The Psychology of Money")
                .authorSurname("Housel")
                .authorInitials("M.")
                .publicationYear(2020)
                .copiesCount(5)
                .genre("Finance")
                .language("English")
                .isbn("9780857197689")
                .publisher("Harriman House")
                .description("A book about financial behavior.")
                .status(BookStatus.AVAILABLE)
                .createdBy(admin)
                .updatedBy(admin)
                .build());

        bookRepository.save(Book.builder()
                .title("Atomic Habits")
                .authorSurname("Clear")
                .authorInitials("J.")
                .publicationYear(2018)
                .copiesCount(2)
                .genre("Self-development")
                .language("English")
                .isbn("9780735211292")
                .publisher("Avery")
                .description("A practical guide to habit formation.")
                .status(BookStatus.AVAILABLE)
                .createdBy(admin)
                .updatedBy(admin)
                .build());
    }
}