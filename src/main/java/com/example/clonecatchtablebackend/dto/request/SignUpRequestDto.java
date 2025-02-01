package com.example.clonecatchtablebackend.dto.request;

import com.example.clonecatchtablebackend.domain.user.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SignUpRequestDto(
    @NotBlank(message = "빈 값이 될 수 없습니다.")
    @Size(min = 4, max = 50, message = "아이디는 4글자 이상 50글자 이하로 작성해주세요.")
    String username,

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,256}$", message = "비밀번호는 8자리 이상 가능합니다.")
    String password,

    @NotBlank(message = "빈 값이 될 수 없습니다.")
    @Email
    @Size(min = 6, max = 100, message = "이메일은 100글자 이상 작성할 수 없습니다.")
    String email,

    @NotBlank(message = "빈 값이 될 수 없습니다.")
    @Size(min = 2, max = 50, message = "이름은 2글자 이상 20글자 이하로 작성해주세요.")
    String name,

    Gender gender,
    LocalDate birth,
    String activeArea
) {
}