package org.example.basewebsub.exception;

public class ErrorInfo extends BaseObject {
    private int status;
    private String error;
    private String detailError;
    private String path;
    private String uuid;
    private Object data;

    public ErrorInfo(int status, String error, String path, String uuid, Object data) {
        super();
        this.status = status;
        this.error = error;
        this.path = path;
        this.uuid = uuid;
        this.data = data;
    }

    public ErrorInfo(int status, String error, String detailError, String path, String uuid, Object data) {
        super();
        this.status = status;
        this.error = error;
        this.detailError = detailError;
        this.path = path;
        this.uuid = uuid;
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDetailError() {
        return detailError;
    }

    public void setDetailError(String detailError) {
        this.detailError = detailError;
    }

}

