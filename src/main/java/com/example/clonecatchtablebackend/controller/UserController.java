package com.example.clonecatchtablebackend.controller;

import com.example.clonecatchtablebackend.domain.user.UserService;
import com.example.clonecatchtablebackend.dto.request.LoginRequestDto;
import com.example.clonecatchtablebackend.dto.request.SignUpRequestDto;
import com.example.clonecatchtablebackend.dto.request.UpdatePasswordRequestDto;
import com.example.clonecatchtablebackend.dto.request.UpdateUserRequestDto;
import com.example.clonecatchtablebackend.dto.response.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Description(
        "회원가입"
    )
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Validated @RequestBody SignUpRequestDto request, Errors errors) {
        userService.signUp(request, errors);
        return ResponseEntity.ok().build();
    }

    @Description(
        "로그인"
    )
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto request, Errors errors) {
        return ResponseEntity.ok(userService.login(request, errors));
    }

    @Description(
        "사용자 정보 수정"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMyInfo(@PathVariable Long id, @RequestBody UpdateUserRequestDto update) {
        userService.updateMyInfo(id, update);
        return ResponseEntity.ok().build();
    }

    @Description(
        "비밀번호 변경하기"
    )
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Validated @RequestBody UpdatePasswordRequestDto update, Errors errors) {
        userService.changePassword(update, errors);
        return ResponseEntity.ok().build();
    }
}