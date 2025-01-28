package com.example.clonecatchtablebackend.common.security;

import com.example.clonecatchtablebackend.domain.user.User;
import com.example.clonecatchtablebackend.dto.response.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${jwt.access-expire-time}")
    private Long accessExpireTime;

    @Value("${jwt.refresh-expire-time}")
    private Long refreshExpireTime;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refresh}")
    private String refreshKey;

    private final String Bearer = "Bearer";

    public String accessToken(User user) {
        byte[] accessSecret = this.secretKey.getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
            .claim("id", user.getId())
            .claim("role", user.getRole())
            .claim("type", JwtType.ACCESS)
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime() + this.accessExpireTime))
            .signWith(Keys.hmacShaKeyFor(accessSecret))
            .compact();
    }

    // access token 생성
    public String refreshToken(User user) {
        byte[] refreshSecret = this.refreshKey.getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
            .claim("id", user.getId())
            .claim("type", JwtType.REFRESH)
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime() + this.refreshExpireTime))
            .signWith(Keys.hmacShaKeyFor(refreshSecret))
            .compact();
    }

    public TokenDto generateToken(User user) {
        return new TokenDto(
            Bearer,
            this.accessToken(user),
            this.refreshToken(user)
        );
    }

    // JWT Claims 추출하기
    public Claims parseClaims(String token) {
        byte[] accessSecret = secretKey.getBytes(StandardCharsets.UTF_8);

        try {
            return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(accessSecret))
                .build()
                .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            // TODO: 에러 메시지 적용
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    // JWT 검증하기
    public boolean isValidToken(String token) {
        byte[] accessSecret = secretKey.getBytes(StandardCharsets.UTF_8);

        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(accessSecret))
                .build()
                .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 토큰 정보입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰 정보입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims 부분이 비어있습니다.", e);
        }

        return false;
    }
}