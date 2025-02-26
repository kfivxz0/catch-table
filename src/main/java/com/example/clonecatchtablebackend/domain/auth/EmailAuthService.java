package com.example.clonecatchtablebackend.domain.auth;

import com.example.clonecatchtablebackend.dto.request.EmailAuthRequestDto;
import com.example.clonecatchtablebackend.dto.request.ValidationRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private static String senderEmail;
    private final AuthRepository authRepository;

    // 6자리 랜덤 인증코드 발생
    @Transactional
    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(2);

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 1 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    // 인증코드를 보낼 메일 생성
    @Transactional
    public MimeMessage createMail(EmailAuthRequestDto request, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, request.email());
        message.setSubject("이메일 인증 코드.");
        String body = "";
        body += "<h3>요청하신 인증 코드 입니다.</h3>";
        body += "<h1>"+ authCode +"</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    //     인증코드 발송 후 저장
    @Transactional
    public void sendAuthCode(EmailAuthRequestDto request) throws MessagingException {
        String authCode = createCode();
        MimeMessage message = createMail(request, authCode);
        try {
            javaMailSender.send(message);
            saveAuthCode(request, authCode);
        } catch (MailException e) {
            e.getMessage();
        }
    }

    // 인증코드를 DB에 저장 (DB에 이메일 존재하는 이메일이면 인증코드만 갱신)
    @Transactional
    public void saveAuthCode(EmailAuthRequestDto request, String authCode) {
        Optional<EmailAuth> existingEmailAuth = authRepository.findByEmail(request.email());

        if (existingEmailAuth.isPresent()) {
            // 이메일이 이미 존재하면 인증코드만 갱신
            EmailAuth emailAuth = existingEmailAuth.get();
            emailAuth.patch(authCode);
            log.info("이메일 {} 에 대한 인증 코드 갱신 성공", request.email());
            authRepository.save(emailAuth);
        } else {
            // 이메일이 없으면 새로 저장
            EmailAuth emailAuth = new EmailAuth(request.email(), authCode);
            log.info("이메일 {} 에 대한 인증 코드 저장 성공", request.email());
            authRepository.save(emailAuth);
        }
    }

    // 인증 절차 수행
    @Transactional
    public void validationAuthCode(ValidationRequestDto request) {
        Optional<EmailAuth> emailAuth = authRepository.findByEmail(request.email());

        if (emailAuth.isEmpty()) {
            log.error("이메일 {} 에 대한 인증 정보가 존재하지 않습니다.", request.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일에 해당하는 인증 코드가 존재하지 않습니다.");
        }

        if (!emailAuth.get().getAuthCode().equals(request.authCode())) {
            log.error("인증 코드 불일치: 이메일 {} 에서 제공된 인증 코드가 유효하지 않습니다.", request.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다.");
        }

        authRepository.delete(emailAuth.get());
        log.info("이메일 {} 에 대한 인증 코드 확인 성공", request.email());
    }
}