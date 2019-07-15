package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {

    public BadRequestException(HttpStatus badRequest, String message) {
        super(badRequest, message);
    }
}
