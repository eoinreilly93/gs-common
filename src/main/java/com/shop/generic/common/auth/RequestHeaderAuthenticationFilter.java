package com.shop.generic.common.auth;

import static com.shop.generic.common.auth.HttpUtils.buildError;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class RequestHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final JWTHelper jwtHelper;
    private final String[] ignoreUrls;
    private final JWTTokenEncryptionService jwtTokenEncryptionService;

    public RequestHeaderAuthenticationFilter(final JWTHelper jwtHelper, final String[] ignoreUrls,
            final JWTTokenEncryptionService jwtTokenEncryptionService) {
        this.jwtHelper = jwtHelper;
        this.ignoreUrls = ignoreUrls;
        this.jwtTokenEncryptionService = jwtTokenEncryptionService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain chain) throws ServletException, IOException {
        log.debug("Processing request authentication...");
        final String encryptedToken = httpServletRequest.getHeader("Bearer-Token");
        log.info("Received encrypted bearer token: {}", encryptedToken);
        String decryptedToken = "";
        //Decrypt the token
        log.debug("Decrypting the JWT token");
        decryptedToken = jwtTokenEncryptionService.decryptBearerToken(encryptedToken);
        log.info("Decrypted the JWT token: {}", decryptedToken);
        final boolean isRequestFromIgnoredUrls = Arrays.stream(ignoreUrls)
                .anyMatch(url -> new AntPathRequestMatcher(url).matches(httpServletRequest));
        if (isRequestFromIgnoredUrls) {
            log.debug("Skipping authentication filter as request path {} is from ignored urls...",
                    httpServletRequest.getServletPath());
            chain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            if (!StringUtils.isEmpty(decryptedToken)) {
                try {
                    log.debug("Checking JWT token validity");
                    if (jwtHelper.isValidToken(decryptedToken)) {
                        final Permissions context = jwtHelper.getContext(decryptedToken);
                        final PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                                null, null, null);
                        authentication.setDetails(context);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        chain.doFilter(httpServletRequest, httpServletResponse);
                    } else {
                        log.warn("Invalid JWT was presented... sending 401 response");
                        buildError("Invalid jwt", HttpStatus.UNAUTHORIZED,
                                LocalDateTime.now(), httpServletRequest, httpServletResponse);
                    }
                } catch (final ExpiredJwtException e) {
                    log.error("Expired JWT", e);
                    buildError(e.getMessage(), HttpStatus.UNAUTHORIZED,
                            LocalDateTime.now(), httpServletRequest, httpServletResponse);
                } catch (final Exception e) {
                    log.error("Error while validating JWT", e);
                    buildError("Failed to process JWT", HttpStatus.UNAUTHORIZED,
                            LocalDateTime.now(), httpServletRequest, httpServletResponse);
                }
            } else {
                log.warn("Received request without token... sending 401 response");
                buildError("Invalid token", HttpStatus.UNAUTHORIZED,
                        LocalDateTime.now(), httpServletRequest, httpServletResponse);
            }
        }
    }
}
