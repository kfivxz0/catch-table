package com.example.clonecatchtablebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ValidationRequestDto(
     @NotBlank(message = "이메일을 입력하세요")
     @Pattern(regexp = "^[A-Za-z0-9]+([._%+-]*[A-Za-z0-9]+)*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "잘못된 이메일 형식입니다.")
     String email,

    @NotBlank(message = "인증번호를 입력하세요")
    String authCode
) {
}
