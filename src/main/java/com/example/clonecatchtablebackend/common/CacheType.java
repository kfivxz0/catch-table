package com.example.clonecatchtablebackend.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    LOGIN_USER("loginUser", 180, 500),
    AUTHENTICATED_USER("authenticatedUser", 300, 500);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}