package com.example.clonecatchtablebackend.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidationErrors {
    public void checkDtoErrors(Errors errors, String message) {
        if (errors.hasErrors()) {
            for (var error : errors.getFieldErrors()) {
                log.error("Field error in object '{}': field='{}', rejected value='{}', message='{}'",
                    error.getObjectName(), error.getField(), error.getRejectedValue(), error.getDefaultMessage());
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}