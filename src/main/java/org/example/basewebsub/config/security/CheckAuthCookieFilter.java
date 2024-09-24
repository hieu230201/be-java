package org.example.basewebsub.config.security;


import org.apache.logging.log4j.ThreadContext;
import org.example.basewebsub.comon.SubConstants;
import org.example.basewebsub.config.HttpInfo;
import org.example.basewebsub.config.RequestWrapper;
import org.example.basewebsub.config.ResponseWrapper;
import org.example.basewebsub.logging.LogManage;
import org.example.basewebsub.logging.bases.ILogManage;
import org.example.basewebsub.logging.common.MessageLog;
import org.example.basewebsub.util.HttpUtil;
import org.example.basewebsub.util.JsonConvertUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * kiểm tra request của người dùng trước khi nó tới controller
 *
 * @author hieunt
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CheckAuthCookieFilter extends OncePerRequestFilter {

    private static final ILogManage logManage = LogManage.getLogManage(CheckAuthCookieFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();

        final RequestWrapper requestWrapper = new RequestWrapper(uuid, start, request);
        final ResponseWrapper responseWrapper = new ResponseWrapper(uuid, response);
        HttpInfo httpInfo = HttpUtil.getHttpInfo(requestWrapper);
        logRequest(requestWrapper, uuid, httpInfo);
        ThreadContext.put(SubConstants.UU_ID, uuid);
        ThreadContext.put(SubConstants.START_TIME, String.valueOf(start));
        ThreadContext.put(SubConstants.PATH, request.getRequestURI());
        filterChain.doFilter(requestWrapper, responseWrapper);
        ThreadContext.remove(SubConstants.UU_ID);
        ThreadContext.remove(SubConstants.START_TIME);
        ThreadContext.remove(SubConstants.PATH);

    }

    private void logRequest(HttpServletRequest request, String uuid, HttpInfo httpInfo) {
        MessageLog messageLog = new MessageLog();
        messageLog.setClassName(this.getClass().getName());
        messageLog.setMethodName(request.getMethod() + "|logRequest");
        messageLog.setId(uuid);
        messageLog.setPath(request.getRequestURI());
        try {
            messageLog.setPath(request.getRequestURI());
            Map messageMap = new HashMap();
            messageMap.put(SubConstants.BODY, JsonConvertUtil.convertObjectToJson(httpInfo));
            messageLog.setMessages(messageMap);
            logManage.info(messageLog);
        } catch (Exception e) {
            messageLog.setMessage(
                    String.format("logRequest %s - Error: %s", request.getMethod(), request.getRequestURI()));
            messageLog.setException(e);
            logManage.error(messageLog, new LogManage.Configuration(true, false));
        }
    }
}
