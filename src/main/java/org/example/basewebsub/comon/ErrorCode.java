package org.example.basewebsub.comon;

import org.example.basewebsub.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Cấu hình erorcode - error message - httpStatus code
 *
 * @author hieunt
 */
public enum ErrorCode implements BaseErrorCode {
    // auth
    TOKEN_INVALID(8, "token.invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(9, "token.expired", HttpStatus.UNAUTHORIZED),
    NO_RIGHTS_EXECUTE(1006, "no.rights.execute", HttpStatus.METHOD_NOT_ALLOWED),
    HTTP_STATUS_CODE_EXCEPTION(1007, "", HttpStatus.BAD_REQUEST),
    SERVER_ERROR(13, "server.error", HttpStatus.UNPROCESSABLE_ENTITY),
    USER_NOT_FOUND(7, "username.not.found", HttpStatus.UNPROCESSABLE_ENTITY),
    UNPROCESSABLE_ENTITY(1008, "", HttpStatus.UNPROCESSABLE_ENTITY);


    private final int code;

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}