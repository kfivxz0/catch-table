package com.example.clonecatchtablebackend.domain.user;

import com.example.clonecatchtablebackend.dto.request.UpdateUserRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false,
        length = 50,
        unique = true
    )
    private String username;

    @Column(
        nullable = false
    )
    private String password;

    @Column(
        nullable = false,
        length = 30
    )
    private String email;

    @Column(
        length = 20
    )
    private String name;

    @Column(
        length = 10
    )
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;

    @Column(
        nullable = false,
        length = 10
    )
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(
        nullable = false,
        length = 20
    )
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(
        length = 50
    )
    private String activeArea;

    @Column(
        nullable = false,
        length = 10
    )
    @Enumerated(EnumType.STRING)
    private Platform platform;

    private LocalDateTime loggedInAt;

    private Integer loginFailCount;

    private LocalDateTime passwordChangedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void successAuthentication() {
        this.status = Status.COMPLETE;
        this.updatedAt = LocalDateTime.now();
    }

    public void successLogin() {
        this.loginFailCount = 0;
        this.loggedInAt = LocalDateTime.now();
    }

    public void failLogin() {
        this.loginFailCount++;
    }

    public void updateStatusStopped() {
        this.status = Status.STOPPED;
    }

    public void update(UpdateUserRequestDto update) {
        if (update.name() != null) {
            this.name = update.name();
        }

        if (update.birth() != null) {
            this.birth = update.birth();
        }

        if (update.activeArea() != null) {
            this.activeArea = update.activeArea();
        }
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}