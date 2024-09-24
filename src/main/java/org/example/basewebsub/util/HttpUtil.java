package org.example.basewebsub.util;


import org.example.basewebsub.config.HttpInfo;
import org.example.basewebsub.config.RequestWrapper;
import org.example.basewebsub.config.ResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {


    public static HttpInfo getHttpInfo(RequestWrapper requestWrapper) throws UnsupportedEncodingException {
        HttpInfo httpInfo = new HttpInfo();
        httpInfo.setUrl(getUrl(requestWrapper));
        httpInfo.setRequestHeaders(getRequestHeaders(requestWrapper));
        httpInfo.setRequestBody(getRequestBody(requestWrapper));
        return httpInfo;
    }

    public static Map<String, String> getRequestHeaders(RequestWrapper requestWrapper) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(requestWrapper.getHeaderNames())
                .forEach(key -> headers.put((String) key, requestWrapper.getHeader((String) key)));
        return headers;
    }

    public static String getUrl(RequestWrapper requestWrapper) {
        return requestWrapper.getRequestURL() + "?" + requestWrapper.getQueryString();
    }

    public static String getRequestBody(RequestWrapper requestWrapper) throws UnsupportedEncodingException {
        return requestWrapper.getBody();
    }

}