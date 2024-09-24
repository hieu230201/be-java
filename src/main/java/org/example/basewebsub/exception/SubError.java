package org.example.basewebsub.exception;

public class SubError {
    private String fieldName;

    private Object fieldValue;

    private final String message;

    public SubError(String fieldName, Object fieldValue, String message) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getMessage() {
        return message;
    }

}