package com.library.management.config;

import com.library.management.entity.*;
import com.library.management.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the database with an admin account, sample users, and 12+ books.
 * Runs only once — guards against re-seeding by checking if admin already exists.
 * Passwords are hashed via BCrypt here at runtime; no plain text is ever stored.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      BookRepository bookRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.bookRepository  = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@library.com")) {
            log.info("Seed data already present — skipping.");
            return;
        }
        log.info("Seeding database…");
        seedUsers();
        seedBooks();
        log.info("Seeding complete.");
    }

    private void seedUsers() {
        List<User> users = List.of(
            User.builder().name("Admin").email("admin@library.com")
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .role(User.Role.ROLE_ADMIN).phone("9000000001").enabled(true).build(),
            User.builder().name("Alice Sharma").email("alice@example.com")
                .passwordHash(passwordEncoder.encode("Alice@123"))
                .role(User.Role.ROLE_USER).phone("9000000002").enabled(true).build(),
            User.builder().name("Bob Kumar").email("bob@example.com")
                .passwordHash(passwordEncoder.encode("Bob@12345"))
                .role(User.Role.ROLE_USER).phone("9000000003").enabled(true).build(),
            User.builder().name("Carol Singh").email("carol@example.com")
                .passwordHash(passwordEncoder.encode("Carol@123"))
                .role(User.Role.ROLE_USER).phone("9000000004").enabled(true).build(),
            User.builder().name("David Rao").email("david@example.com")
                .passwordHash(passwordEncoder.encode("David@123"))
                .role(User.Role.ROLE_USER).phone("9000000005").enabled(true).build()
        );
        userRepository.saveAll(users);
    }

    private void seedBooks() {
        List<Book> books = List.of(
            // Java
            book("Effective Java", "Joshua Bloch", "978-0134685991", "Java",
                 "Addison-Wesley", (short)2018, 3,
                 "Best practices for the Java platform."),
            book("Java: The Complete Reference", "Herbert Schildt", "978-1260440232", "Java",
                 "McGraw-Hill", (short)2021, 2,
                 "Comprehensive guide to Java programming."),
            // Programming
            book("Clean Code", "Robert C. Martin", "978-0132350884", "Programming",
                 "Prentice Hall", (short)2008, 4,
                 "A handbook of agile software craftsmanship."),
            book("The Pragmatic Programmer", "David Thomas & Andrew Hunt", "978-0135957059", "Programming",
                 "Addison-Wesley", (short)2019, 2,
                 "Timeless wisdom for software developers."),
            // Database
            book("Database System Concepts", "Silberschatz, Korth, Sudarshan", "978-0078022159", "Database",
                 "McGraw-Hill", (short)2019, 3,
                 "Foundational text on database management systems."),
            book("Learning SQL", "Alan Beaulieu", "978-1492057611", "Database",
                 "O'Reilly", (short)2020, 2,
                 "Master SQL fundamentals with hands-on examples."),
            // Web Development
            book("HTML and CSS: Design and Build Websites", "Jon Duckett", "978-1118008188", "Web Development",
                 "Wiley", (short)2011, 3,
                 "Visually stunning introduction to web design."),
            book("JavaScript: The Good Parts", "Douglas Crockford", "978-0596517748", "Web Development",
                 "O'Reilly", (short)2008, 2,
                 "Uncover the beauty and brilliance of JavaScript."),
            // Computer Science
            book("Introduction to Algorithms", "Cormen, Leiserson, Rivest, Stein", "978-0262046305", "Computer Science",
                 "MIT Press", (short)2022, 2,
                 "The definitive textbook on algorithms."),
            // Fiction
            book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "978-0345391803", "Fiction",
                 "Del Rey", (short)1995, 5,
                 "A comic science fiction masterpiece."),
            // Science
            book("A Brief History of Time", "Stephen Hawking", "978-0553380163", "Science",
                 "Bantam", (short)1998, 3,
                 "From the Big Bang to black holes."),
            // History
            book("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "978-0062316110", "History",
                 "Harper", (short)2015, 4,
                 "How Homo sapiens came to rule the world.")
        );
        bookRepository.saveAll(books);
    }

    private Book book(String title, String author, String isbn, String category,
                      String publisher, short year, int qty, String desc) {
        return Book.builder()
                .title(title).author(author).isbn(isbn).category(category)
                .publisher(publisher).publishYear(year)
                .totalQuantity(qty).availableQuantity(qty)
                .description(desc)
                .build();
    }
}
