package org.example.basewebsub.config;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

public class RequestWrapper extends HttpServletRequestWrapper {
    private String uuid;
    private long startTime;
    private final String body;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public RequestWrapper(String uuid, long startTime, HttpServletRequest request) throws IOException {
        super(request);
        this.customHeaders = new HashMap();
        this.uuid = uuid;
        this.startTime = startTime;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
// Store request pody content in 'body' variable
        body = stringBuilder.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    // Use this method to read the request body N times
    public String getBody() {
        return this.body;
    }

    // holds custom header and value mapping
    private final Map customHeaders;

    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    public String getHeader(String name) {
// check the custom headers first
        String headerValue = (String) customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }
// else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public Enumeration getHeaderNames() {
// create a set of the custom header names
        Set set = new HashSet(customHeaders.keySet());

// now add the headers from the wrapped request object
        Enumeration e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
// add the names of the request headers into the list
            String n = (String) e.nextElement();
            set.add(n);
        }

// create an enumeration from the set and return
        return Collections.enumeration(set);
    }
}
