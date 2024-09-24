package org.example.basewebsub.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.logging.log4j.ThreadContext;
import org.example.basewebsub.util.StringUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @param <T>
 * @author hieunt
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ResponseData<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5435628560059929L;

    private int status;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+7")
    private final Date timeResponse;

    private String uuid;

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long duration;

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    private T data;

    public ResponseData() {
        this.timeResponse = new Date();
    }

    private String soaErrorCode;

    private String soaErrorDesc;

    private String error;

    public void setSoaErrorCode(String soaErrorCode) {
        this.soaErrorCode = soaErrorCode;
    }

    public void setSoaErrorDesc(String soaErrorDesc) {
        this.soaErrorDesc = soaErrorDesc;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ResponseData<T> success(T data) {
        this.data = data;
        this.status = 0;
        this.message = "Success!";
        this.path = ThreadContext.get("path");
        this.uuid = ThreadContext.get("uuid");
        long start = Long.parseLong(StringUtil.isNullOrEmpty(ThreadContext.get("startTime")) ? "0" : ThreadContext.get("startTime"));
        this.duration = System.currentTimeMillis() - start;
        return this;
    }

    public ResponseData<T> error(int code, String message) {
        this.status = code;
        this.message = message;
        this.path = ThreadContext.get("path");
        this.uuid = ThreadContext.get("uuid");
        long start = Long.parseLong(StringUtil.isNullOrEmpty(ThreadContext.get("startTime")) ? "0" : ThreadContext.get("startTime"));
        this.duration = System.currentTimeMillis() - start;
        return this;
    }

    public ResponseData<T> error(int code, String message, T data) {
        this.status = code;
        this.message = message;
        this.data = data;
        this.path = ThreadContext.get("path");
        this.uuid = ThreadContext.get("uuid");
        long start = Long.parseLong(StringUtil.isNullOrEmpty(ThreadContext.get("startTime")) ? "0" : ThreadContext.get("startTime"));
        this.duration = System.currentTimeMillis() - start;
        return this;
    }

    public ResponseData<T> error(int code, String message, T data, String soaErrorCode, String soaErrorDesc, String error) {
        this.status = code;
        this.message = message;
        this.data = data;
        this.path = ThreadContext.get("path");
        this.uuid = ThreadContext.get("uuid");
        long start = Long.parseLong(StringUtil.isNullOrEmpty(ThreadContext.get("startTime")) ? "0" : ThreadContext.get("startTime"));
        this.duration = System.currentTimeMillis() - start;
        this.soaErrorCode = soaErrorCode;
        this.soaErrorDesc = soaErrorDesc;
        this.error = error;
        return this;
    }

}

