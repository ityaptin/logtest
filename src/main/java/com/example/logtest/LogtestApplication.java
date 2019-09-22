package com.example.logtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static com.example.logtest.CorrelationInterceptor.CORRELATION_ID_HEADER_NAME;
import static com.example.logtest.CorrelationInterceptor.CORRELATION_ID_LOG_VAR_NAME;

@SpringBootApplication
@EnableFeignClients
public class LogtestApplication {

    private final ObjectMapper objectMapper;

    public LogtestApplication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogtestApplication.class, args);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(CORRELATION_ID_HEADER_NAME, MDC.get(CORRELATION_ID_LOG_VAR_NAME));
        };
    }

    @Bean
    public AbstractRequestLoggingFilter requestLoggingFilter() {
        CustomRequestLoggingFilter loggingFilter = new CustomRequestLoggingFilter(objectMapper);
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(1000);
        return loggingFilter;
    }

}
