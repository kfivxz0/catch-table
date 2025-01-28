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
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (Arrays.stream(SecurityPath.WHITE_LIST).anyMatch(white -> pathMatcher.match(white, request.getRequestURI()))) {
            log.info("{}, WHITE_LIST 포함", request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

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
            chain.doFilter(request, response);
        } else {
            // 토큰이 없거나 유효하지 않은 경우 403 상태 코드와 메시지를 반환
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("접근이 허용되지 않습니다. 유효한 토큰을 제공하십시오.");
            response.getWriter().flush();
        }
    }
}