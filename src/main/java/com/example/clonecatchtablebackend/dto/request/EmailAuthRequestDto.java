package com.example.clonecatchtablebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailAuthRequestDto(
    @NotBlank(message = "이메일을 입력하세요")
    @Pattern(regexp = "^[A-Za-z0-9]+([._%+-]*[A-Za-z0-9]+)*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "잘못된 이메일 형식입니다.")
    String email
) {
}
