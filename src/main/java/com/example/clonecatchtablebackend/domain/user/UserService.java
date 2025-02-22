package com.example.clonecatchtablebackend.domain.user;

import com.example.clonecatchtablebackend.common.security.JwtUtils;
import com.example.clonecatchtablebackend.dto.request.LoginRequestDto;
import com.example.clonecatchtablebackend.dto.request.SignUpRequestDto;
import com.example.clonecatchtablebackend.dto.request.UpdatePasswordRequestDto;
import com.example.clonecatchtablebackend.dto.request.UpdateUserRequestDto;
import com.example.clonecatchtablebackend.dto.response.TokenDto;
import com.example.clonecatchtablebackend.exception.ValidationErrors;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtils jwtUtils;
    private final CacheManager cacheManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationErrors validationErrors;

    public User loginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        // 먼저 캐시에서 유저 정보 조회
        Cache cache = cacheManager.getCache("loginUser");
        User cachedUser = cache.get(userId, User.class); // 캐시에서 유저 정보 찾기

        if (cachedUser != null) {
            log.info("캐시된 유저 반환 성공");
            return cachedUser;
        }

        // 캐시된 유저가 없으면 DB 에서 조회 후 캐시 저장
        User user = userRepository.findById(userId)
            .orElseThrow();

        cache.put(userId, user); // 캐시 저장
        log.info("{} - {}, 로그인 유저 캐시 저장", cache.getName(), userId);

        return user;
    }

    @Transactional
    public void signUp(SignUpRequestDto request, Errors errors) {
        if (userRepository.existsByUsernameOrEmail(request.username(), request.email())) {
            log.error("이미 사용중인 아이디 또는 이메일 입니다.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or Email is Already in Use");
        }

        User newUser = User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .email(request.email())
            .name(request.name())
            .gender(request.gender())
            .birth(request.birth())
            .role(Role.USER)
            .status(Status.WAITING)
            .activeArea(request.activeArea())
            .platform(Platform.BASIC)
            .loginFailCount(0)
            .createdAt(LocalDateTime.now())
            .build();

        userRepository.save(newUser);
        log.info("ID: {}, 회원가입 완료", newUser.getUsername());
    }

    @Transactional
    public TokenDto login(LoginRequestDto request, Errors errors) {
        // TODO: Validation 에러 메시지 적용하기

        // TODO: 유저가 없을 경우 에러 메시지 적용하기
        User user = userRepository.findByUsername(request.username())
            .orElseThrow();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.error("사용자 로그인 오류");
            user.failLogin();

            if (user.getLoginFailCount() > 5) {
                log.error("실패 횟수 5회 초과 사용자");
                user.updateStatusStopped();
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // TODO: 이메일 인증 단계 추가 후, 활성화
//        if (user.getStatus() != Status.COMPLETE) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 완료되지 않은 사용자이거나 정지된 사용자입니다.");
//        }

        user.successLogin();

        log.info("사용자:{}, 로그인 성공", user.getId());
        return jwtUtils.generateToken(user);
    }

    @Transactional
    @CachePut(value = "authenticatedUser", key = "#id")
    public void updateMyInfo(Long id, UpdateUserRequestDto update) {
        // TODO: 유저가 없을 경우 에러메시지 적용
        User user = userRepository.findById(id)
            .orElseThrow();

        user.update(update);
        log.info("사용자: {}, 정보 수정 완료", id);
    }

    @Transactional
    public void changePassword(UpdatePasswordRequestDto update, Errors errors) {
        User user = this.loginUser();

        if (passwordEncoder.matches(update.newPassword(), user.getPassword())) {
            log.error("사용자: {}, 같은 비밀번호로 변경 시도", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "같은 비밀번호로는 변경이 불가능합니다.");
        }

        String newPassword = passwordEncoder.encode(update.newPassword());
        user.changePassword(newPassword);
        userRepository.save(user);
        log.info("사용자: {}, 비밀번호 변경 완료", user.getId());

        Cache cache = cacheManager.getCache("loginUser");

        if (cache != null) {
            cache.evict(user.getId());
            log.info("비밀번호 변경 유저: {}, 캐시 삭제 완료", user.getId());
        }
    }
}