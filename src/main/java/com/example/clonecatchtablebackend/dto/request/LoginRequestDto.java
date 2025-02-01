package com.example.clonecatchtablebackend.dto.request;

public record LoginRequestDto(
    String username,
    String password
) {
}