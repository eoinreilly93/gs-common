package com.shop.generic.common;

import com.shop.generic.common.auth.MicroservicRestTemplateAuthInterceptor;
import com.shop.generic.common.auth.MicroserviceAuthorisationService;
import com.shop.generic.common.auth.ServiceContext;
import com.shop.generic.common.clock.ApplicationClock;
import com.shop.generic.common.clock.GsClock;
import com.shop.generic.common.rest.errorhandlers.ExceptionHandlerControllerAdvice;
import com.shop.generic.common.rest.interceptors.MicroserviceRestTemplateInterceptor;
import com.shop.generic.common.rest.request.RestTemplateUtil;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class CommonAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Setting up Common Configuration");
    }

    @Bean
    public ServiceContext serviceContext() {
        return new ServiceContext();
    }

    @Bean
    public GsClock clock() {
        return new ApplicationClock();
    }

    @Bean
    public RestApiResponseFactory restApiResponseFactory() {
        return new RestApiResponseFactory(clock());
    }

    @Bean
    public RestTemplateUtil restTemplateUtil(
            @Autowired(required = false) final MicroserviceRestTemplateInterceptor microserviceRestTemplateInterceptor,
            @Autowired(required = false) final MicroservicRestTemplateAuthInterceptor microservicRestTemplateAuthInterceptor) {
        return new RestTemplateUtil(restTemplate(microserviceRestTemplateInterceptor,
                microservicRestTemplateAuthInterceptor));
    }

    //TODO: Figure out a better way to handle the different interceptors depending on whether auth is enabled or not
    @Bean
    public RestTemplate restTemplate(
            @Autowired(required = false) final MicroserviceRestTemplateInterceptor microserviceRestTemplateInterceptor,
            @Autowired(required = false) final MicroservicRestTemplateAuthInterceptor microservicRestTemplateAuthInterceptor) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(120000);
        requestFactory.setConnectTimeout(60000);
        final RestTemplate restTemplate = new RestTemplate(requestFactory);

        final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        // Conditionally add the appropriate interceptor
        if (microserviceRestTemplateInterceptor != null) {
            interceptors.add(microserviceRestTemplateInterceptor);
        } else if (microservicRestTemplateAuthInterceptor != null) {
            interceptors.add(microservicRestTemplateAuthInterceptor);
        }

        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    @Bean
    @ConditionalOnProperty(prefix = "gsshop-common.auth", name = "enabled", havingValue = "false")
    public MicroserviceRestTemplateInterceptor microserviceRestTemplateInterceptor() {
        log.info(
                "Setting up MicroserviceRestTemplateInterceptor as 'gsshop-common.auth.enabled' is false");
        return new MicroserviceRestTemplateInterceptor();
    }

    @Bean
    public MicroserviceAuthorisationService microserviceAuthorisationService() {
        return new MicroserviceAuthorisationService();
    }

    @Bean
    public ExceptionHandlerControllerAdvice exceptionHandlerControllerAdvice() {
        return new ExceptionHandlerControllerAdvice(restApiResponseFactory());
    }

}