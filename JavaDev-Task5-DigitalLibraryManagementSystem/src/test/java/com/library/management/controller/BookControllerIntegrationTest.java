package com.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.dto.BookRequest;
import com.library.management.entity.Book;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long savedBookId;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        Book book = Book.builder()
                .title("Effective Java 3rd Edition")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .category("Java")
                .publisher("Addison-Wesley")
                .publishYear((short) 2018)
                .totalQuantity(5)
                .availableQuantity(5)
                .build();
        Book saved = bookRepository.save(book);
        savedBookId = saved.getId();
    }

    @Test
    @DisplayName("GET /api/books - public access returns 200 and page of books")
    void getBooks_PublicAccess_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", containsString("Effective Java")));
    }

    @Test
    @DisplayName("GET /api/books/{id} - returns 200 with book details")
    void getBookById_Exists_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/books/" + savedBookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBookId))
                .andExpect(jsonPath("$.isbn").value("978-0134685991"));
    }

    @Test
    @DisplayName("GET /api/books/{id} - not found returns 404 via GlobalExceptionHandler")
    void getBookById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/books/999999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Book not found")));
    }

    @Test
    @DisplayName("POST /api/books - unauthenticated returns 401 Unauthorized")
    void createBook_Unauthenticated_Returns401() throws Exception {
        BookRequest req = new BookRequest("Clean Code", "Robert Martin", "978-0132350884", "Programming", "Prentice", 2008, null, 3);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/books - authenticated as ROLE_USER returns 403 Forbidden")
    @WithMockUser(username = "alice@example.com", roles = {"USER"})
    void createBook_AsUser_Returns403() throws Exception {
        BookRequest req = new BookRequest("Clean Code", "Robert Martin", "978-0132350884", "Programming", "Prentice", 2008, null, 3);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/books - authenticated as ROLE_ADMIN creates book (201 Created)")
    @WithMockUser(username = "admin@library.com", roles = {"ADMIN"})
    void createBook_AsAdmin_Returns201() throws Exception {
        BookRequest req = new BookRequest("Clean Code", "Robert Martin", "978-0132350884", "Programming", "Prentice", 2008, null, 3);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.totalQuantity").value(3))
                .andExpect(jsonPath("$.availableQuantity").value(3));
    }
}
