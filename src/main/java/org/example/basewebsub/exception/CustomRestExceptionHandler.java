package org.example.basewebsub.exception;


import org.example.basewebsub.comon.ErrorCode;
import org.example.basewebsub.model.BaseObjectInfo;
import org.example.basewebsub.response.ResponseData;
import org.example.basewebsub.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    // @Validate For Validating Path Variables and Request Parameters
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex,
                                                               HttpServletResponse response) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            strBuilder.append(System.lineSeparator());
            strBuilder.append(violation.getMessage());
        }
        ex.getConstraintViolations().parallelStream().map(e -> e.getMessage())
                .collect(Collectors.joining(System.lineSeparator()));
        return new ResponseEntity<>(
                new ResponseData<>().error(ErrorCode.UNPROCESSABLE_ENTITY.getCode(), strBuilder.toString(), null),
                HttpStatus.BAD_REQUEST);
    }

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<SubError> lstSubErr = new ArrayList<>();
        List<FieldError> fieldError = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors();
        StringBuilder builder = new StringBuilder();

        for (FieldError error : fieldError) {
            SubError subError = new SubError(error.getField(), error.getRejectedValue(), error.getDefaultMessage());
            lstSubErr.add(subError);
            String objectName = "";
            if (error.getRejectedValue() instanceof BaseObjectInfo baseObjectInfo) {
                objectName = baseObjectInfo.getObjectName();
            }
            if (!builder.toString().contains(Objects.requireNonNull(error.getDefaultMessage()))) {
                builder.append(System.lineSeparator());
                if (StringUtil.isNotNullAndEmpty(objectName))
                    builder.append(objectName).append(": ");
                builder.append(error.getDefaultMessage());
            }
        }
        return new ResponseEntity<>(
                new ResponseData<>().error(ErrorCode.UNPROCESSABLE_ENTITY.getCode(), builder.toString(), lstSubErr),
                status);

    }

}