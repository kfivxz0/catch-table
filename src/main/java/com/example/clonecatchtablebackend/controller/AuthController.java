package com.example.clonecatchtablebackend.controller;

import com.example.clonecatchtablebackend.domain.auth.EmailAuthService;
import com.example.clonecatchtablebackend.dto.request.EmailAuthRequestDto;
import com.example.clonecatchtablebackend.dto.request.ValidationRequestDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final EmailAuthService emailAuthService;

    @Description(
            "인증코드 요청"
    )
    @PostMapping("/sendAuthCode")
    public ResponseEntity<String> sendAuthCode(@RequestBody EmailAuthRequestDto request) throws MessagingException {
            // 인증코드 발송
            emailAuthService.sendAuthCode(request);
            return ResponseEntity.ok().build();

    }

    @Description(
            "인증코드 확인"
    )
    @PostMapping("/validationAuthCode")
    public ResponseEntity<String> validationAuthCode(@RequestBody ValidationRequestDto request) {
            // 인증코드 발송
            emailAuthService.validationAuthCode(request);
            return ResponseEntity.ok().build();
    }
}
