package com.shop.generic.common;

import com.shop.generic.common.rest.errorhandlers.ExceptionHandlerControllerAdvice;
import com.shop.generic.common.rest.interceptors.MicroserviceRestTemplateInterceptor;
import com.shop.generic.common.rest.request.RestTemplateUtil;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonAutoConfiguration {

    @Bean
    public RestApiResponseFactory restApiResponseFactory() {
        return new RestApiResponseFactory();
    }

    @Bean
    public RestTemplateUtil restTemplateUtil() {
        return new RestTemplateUtil(restTemplate());
    }

    @Bean
    public RestTemplate restTemplate() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(120000);
        requestFactory.setConnectTimeout(60000);
        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setInterceptors(List.of(microserviceRestTemplateInterceptor()));
        return restTemplate;
    }

    @Bean
    public MicroserviceRestTemplateInterceptor microserviceRestTemplateInterceptor() {
        return new MicroserviceRestTemplateInterceptor();
    }

    @Bean
    public ExceptionHandlerControllerAdvice exceptionHandlerControllerAdvice() {
        return new ExceptionHandlerControllerAdvice(restApiResponseFactory());
    }
}