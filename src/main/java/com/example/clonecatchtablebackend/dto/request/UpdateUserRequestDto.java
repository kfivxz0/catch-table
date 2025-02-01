package com.example.clonecatchtablebackend.dto.request;

import java.time.LocalDate;

public record UpdateUserRequestDto(
    String name,
    LocalDate birth,
    String activeArea
) {
}