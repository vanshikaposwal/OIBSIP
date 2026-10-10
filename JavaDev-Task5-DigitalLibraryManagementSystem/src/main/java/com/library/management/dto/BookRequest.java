package com.library.management.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) for creating or updating a Book.
 * 
 * In Spring Boot, DTOs decouple external HTTP request payloads from internal database entities.
 * Bean Validation annotations (@NotBlank, @Size, @Min, @Max) ensure data integrity before
 * the request reaches service logic.
 */
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
