package com.example.clonecatchtablebackend.domain.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false
    )
    private String email;

    @Column(
            nullable = false
    )
    private String authCode;

    public EmailAuth(String email, String authCode){
        this.email = email;
        this.authCode = authCode;
    }

    public void patch(String authCode){
        this.authCode = authCode;
    }
}
