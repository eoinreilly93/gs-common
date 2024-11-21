package com.shop.generic.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class MicroservicRestTemplateAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String BEARER_TOKEN = "Bearer-Token";

    private final ServiceContext serviceContext;
    private final JWTHelper jwtHelper;
    private final JwtTokenService jwtTokenService;
    private final JWTTokenEncryptionService jwtTokenEncryptionService;

    public MicroservicRestTemplateAuthInterceptor(final ServiceContext serviceContext,
            final JWTHelper jwtHelper, final JwtTokenService jwtTokenService,
            final JWTTokenEncryptionService jwtTokenEncryptionService) {
        this.serviceContext = serviceContext;
        this.jwtHelper = jwtHelper;
        this.jwtTokenService = jwtTokenService;
        this.jwtTokenEncryptionService = jwtTokenEncryptionService;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
            final ClientHttpRequestExecution execution) throws IOException {
        final long start = System.currentTimeMillis();
        log.info("Sending {} request to microservice {} with data {}", request.getMethod(),
                request.getURI(), new String(body));

        populateHeaders(request);

        final ClientHttpResponse response = execution.execute(request, body);
        final long finish = System.currentTimeMillis();
        //TODO: Consider logging the body here?
        log.info("Response received with {} status in {}ms", response.getStatusCode(),
                finish - start);
        return response;
    }

    private void populateHeaders(final HttpRequest request) {
        log.info("Adding bearer token header");
        final String bearerToken = getBearerToken();
        request.getHeaders().add(BEARER_TOKEN, bearerToken);
        log.info("Set bearer token header with encrypted value {}", bearerToken);
    }

    private String getBearerToken() {
        log.info("Get bearer token");
        return getBearerTokenForSession();
    }

    private String getBearerTokenForSession() {
        final String token;
        log.debug("Get bearer token for service session");
        final String encryptedToken = serviceContext.getBearerToken();
        final String decryptBearerToken = jwtTokenEncryptionService.decryptBearerToken(
                encryptedToken);
        if (!this.jwtHelper.isValidToken(decryptBearerToken)) {
            log.debug("Bearer token is expired or null, generating it...");
            //TODO: Get this input from properties
            token = this.jwtTokenService.generateToken(true);
            serviceContext.setBearerToken(token);
            return token;
        } else {
            log.info("Existing token is valid, re-using it");
        }
        return encryptedToken;
    }

    /**
     * Example of if if this service was an intermediate service e.g. it received a request from one
     * service and then made a request another. Here it would take the bearer token from the first
     * request if it exists, and add it to the new request
     */
    private void dummyInterceptRequestMethod() {
        //This will intercept the current request, and add the bearer token to it if one exists
//        final HttpServletRequest httpServletRequest = getCurrentHttpRequest().orElse(null);
//        if (httpServletRequest != null && httpServletRequest.getHeader(BEARER_TOKEN) != null) {
//            request.getHeaders().add(BEARER_TOKEN, httpServletRequest.getHeader(BEARER_TOKEN));
//        }
    }

    /**
     * Get the current httpServiceRequest
     *
     * @return
     */
    private Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

}
