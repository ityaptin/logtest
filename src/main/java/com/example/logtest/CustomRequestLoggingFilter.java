package com.example.logtest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.MimeHeaders;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple request logging filter that writes the request URI
 * (and optionally the query string) to the Commons Log.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2.5
 * @see #setIncludeQueryString
 * @see #setBeforeMessagePrefix
 * @see #setBeforeMessageSuffix
 * @see #setAfterMessagePrefix
 * @see #setAfterMessageSuffix
 * @see org.apache.commons.logging.Log#debug(Object)
 */
public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {
    public static final String CORRELATION_ID_HEADER_NAME = "X-Request-Id";
    public static final String CORRELATION_ID_LOG_VAR_NAME = "RequestId";

    private final ObjectMapper mapper;

    public CustomRequestLoggingFilter(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return logger.isDebugEnabled();
    }

    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        final String correlationId = getCorrelationIdFromHeader(request);
        MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
//        logger.info(message);
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        try {
            Map<String, Object> req = new HashMap<>();

            String payload = getMessagePayload(request);
            req.put("body", payload);
            req.put("headers", new ServletServerHttpRequest(request).getHeaders());
            req.put("method", request.getMethod());
            req.put("query", request.getQueryString());
            req.put("url", request.getRequestURI());
            logger.info(mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
    }


    private String getCorrelationIdFromHeader(final HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
        if (StringUtils.isBlank(correlationId)) {
            correlationId = generateUniqueCorrelationId();
        }
        return correlationId;
    }

    private String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }
}

