package org.example.basewebsub.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.basewebsub.comon.ErrorCode;
import org.example.basewebsub.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Xử lý những request chưa được xác thực
 *
 * @author hieunt
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("deprecation")
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ResponseData responseData = new ResponseData();
        if (authException.getMessage().toUpperCase().startsWith("ACCESS TOKEN EXPIRED")) {
            String str = "Token đã hết hạn, vui lòng thử lại!";
            byte[] charset = str.getBytes(StandardCharsets.UTF_8);
            String result = new String(charset, StandardCharsets.UTF_8);
            responseData.error(ErrorCode.TOKEN_EXPIRED.getCode(), result);
        } else {
            String str = "Token không đúng vui lòng thử lại!";
            byte[] charset = str.getBytes(StandardCharsets.UTF_8);
            String result = new String(charset, StandardCharsets.UTF_8);
            responseData.error(ErrorCode.TOKEN_INVALID.getCode(), result);
        }

        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(responseData));
    }
}
