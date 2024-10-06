package com.shop.generic.common;

import com.shop.generic.common.rest.response.RestApiResponseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonAutoConfiguration {

    @Bean
    public RestApiResponseFactory restApiResponseFactory() {
        return new RestApiResponseFactory();
    }
}