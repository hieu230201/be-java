package org.example.basewebsub.config;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    @SuppressWarnings("unused")
    private final PrintWriter writer = new PrintWriter(bos);

    public ResponseWrapper(String uuid, HttpServletResponse response) {
        super(response);
        response.setBufferSize(128 * 1024);
    }

    @Override
    public ServletResponse getResponse() {
        return this;
    }

//    @Override
//    public ServletOutputStream getOutputStream() throws IOException {
//        return new ServletOutputStream() {
//            @Override
//            public boolean isReady() {
//                return false;
//            }
//
//            @Override
//            public void setWriteListener(WriteListener writeListener) {
//            }
//
//            private final TeeOutputStream tee = new TeeOutputStream(ResponseWrapper.super.getOutputStream(), bos);
//
//            @Override
//            public void write(int b) throws IOException {
//                tee.write(b);
//            }
//        };
//    }

    public byte[] toByteArray() {
        return bos.toByteArray();
    }
}