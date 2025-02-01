package com.example.clonecatchtablebackend.domain.user;

import lombok.Getter;

@Getter
public enum Platform {
    BASIC("기본"),
    KAKAO("카카오"),
    NAVER("네이버");

    private final String description;

    Platform(String description) {
        this.description = description;
    }
}