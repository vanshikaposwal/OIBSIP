package com.library.management.dto;

import jakarta.validation.constraints.*;

public record BookRequest(
        @NotBlank @Size(max = 300) String title,
        @NotBlank @Size(max = 200) String author,
        @NotBlank @Size(max = 20)  String isbn,
        @NotBlank @Size(max = 80)  String category,
        @Size(max = 200) String publisher,
        @Min(1000) @Max(2100) Integer publishYear,
        String description,
        @Min(1) int totalQuantity
) {}
