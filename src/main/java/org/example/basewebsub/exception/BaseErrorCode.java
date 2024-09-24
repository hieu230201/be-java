package org.example.basewebsub.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    int getCode();

    String getMessage();

    HttpStatus getHttpStatus();
}

