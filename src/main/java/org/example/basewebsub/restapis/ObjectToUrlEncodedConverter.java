package org.example.basewebsub.restapis;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class ObjectToUrlEncodedConverter implements HttpMessageConverter {

    private final ObjectMapper mapper;

    public ObjectToUrlEncodedConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class clazz, MediaType mediaType) {
        return getSupportedMediaTypes().contains(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
    }


    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {
        if (o != null) {
            String body = mapper.convertValue(o, UrlEncodedWriter.class).toString();

            try {
                outputMessage.getBody().write(body.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                // if UTF-8 is not supporter then I give up
            }
        }
    }

    private static class UrlEncodedWriter {
        private final StringBuilder out = new StringBuilder();

        @JsonAnySetter
        public void write(String name, Object property) {
            if (out.length() > 0) {
                out.append("&");
            }

            out.append(URLEncoder.encode(name, StandardCharsets.UTF_8)).append("=");

            if (property != null) {
                out.append(URLEncoder.encode(property.toString(), StandardCharsets.UTF_8));
            }
        }

        @Override
        public String toString() {
            return out.toString();
        }
    }

    @Override
    public Object read(Class clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        throw new NotImplementedException();
    }
}

