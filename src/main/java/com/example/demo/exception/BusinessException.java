package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final HttpStatus badRequest;
    private final String message;

    public BusinessException(HttpStatus badRequest, String message) {

        this.badRequest = badRequest;
        this.message = message;
    }
}
