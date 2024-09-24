package org.example.basewebsub.exception;

import org.example.basewebsub.util.StringUtil;
import org.springframework.http.HttpStatus;

/**
 * Exception xử lý business của hệ thống
 *
 * @author hieunt
 */
public class CustomServiceBusinessException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -5756201794199835339L;
    private final BaseErrorCode errorCode;

    private final String detailMessage;

    private Object[] args;

    public Object[] getArgs() {
        return args;
    }

    public int getCode() {
        return this.errorCode.getCode();
    }

    public CustomServiceBusinessException(BaseErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
        this.detailMessage = "";
    }

    public CustomServiceBusinessException(BaseErrorCode errorCode, Object... params) {
        super();
        this.errorCode = errorCode;
        this.args = params;
        this.detailMessage = "";
    }

    public CustomServiceBusinessException(String message, Throwable cause, BaseErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.detailMessage = message;
    }

    public CustomServiceBusinessException(String message, BaseErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.detailMessage = message;
    }

    public CustomServiceBusinessException(Throwable cause, BaseErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
        this.detailMessage = "";
    }

    @Override
    public String getMessage() {
        if (StringUtil.isNullOrEmpty(this.errorCode.getMessage())) {
            return super.getMessage();
        }
        return this.errorCode.getMessage();
    }

    public String getDetailMessage() {
        return this.detailMessage;
    }

    public HttpStatus getHttpStatus() {
        return this.errorCode.getHttpStatus();
    }
}

