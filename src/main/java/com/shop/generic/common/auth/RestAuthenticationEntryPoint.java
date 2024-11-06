package com.shop.generic.common.auth;

import static com.shop.generic.common.auth.HttpUtils.buildError;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException authException) throws IOException, ServletException {
        log.error("An error has occurred with authentication", authException);
        buildError("Unauthorised request", HttpStatus.UNAUTHORIZED, LocalDateTime.now(), request,
                response);
    }
}
