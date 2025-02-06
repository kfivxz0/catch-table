package com.example.clonecatchtablebackend.common.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityPath {
    public static final String[] WHITE_LIST = {
        "/api/users/login",
        "/api/users/signup"
    };
}