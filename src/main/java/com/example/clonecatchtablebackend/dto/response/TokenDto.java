package com.example.clonecatchtablebackend.dto.response;

public record TokenDto (
    String grantType,
    String accessToken,
    String refreshToken
){
}