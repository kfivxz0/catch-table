package com.example.clonecatchtablebackend.common.security;

import com.example.clonecatchtablebackend.domain.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestUrl = request.getRequestURI();

        if (isWhiteListUrl(requestUrl)) {
            log.info("{}, WHITE_LIST 포함", request.getRequestURI());
            chain.doFilter(request, response);

            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        // JWT 헤더가 있을 경우
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            // 유효성 검증
            if (jwtUtils.isValidToken(token)) {
                Long userId = jwtUtils.getUserIdFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                        );

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isWhiteListUrl(String requestUrl) {
        AntPathMatcher pathMatcher = new AntPathMatcher();

        return Stream.of(SecurityPath.WHITE_LIST)
            .anyMatch(pattern -> pathMatcher.match(pattern, requestUrl));
    }
}