package com.example.clonecatchtablebackend.common.security;

import lombok.Getter;

@Getter
public enum JwtType {
    ACCESS("jwt-access-token"),
    REFRESH("jwt-refresh-token");

    private final String description;

    JwtType(String description) {
        this.description = description;
    }
}