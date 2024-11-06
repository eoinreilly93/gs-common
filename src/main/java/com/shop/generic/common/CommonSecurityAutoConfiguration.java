package com.shop.generic.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.generic.common.auth.JWTHelper;
import com.shop.generic.common.auth.JWTTokenEncryptionService;
import com.shop.generic.common.auth.JwtTokenService;
import com.shop.generic.common.auth.MicroservicRestTemplateAuthInterceptor;
import com.shop.generic.common.auth.RequestHeaderAuthenticationFilter;
import com.shop.generic.common.auth.RestAuthenticationEntryPoint;
import com.shop.generic.common.auth.ServiceContext;
import com.shop.generic.common.properties.AuthProperties;
import com.shop.generic.common.properties.CommonProperties;
import jakarta.annotation.PostConstruct;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;

@ConditionalOnProperty(prefix = "gsshop-common.auth", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(CommonProperties.class)
@Configuration
@Slf4j
public class CommonSecurityAutoConfiguration {

    private final CommonProperties commonProperties;
    private final AuthProperties authProperties;
    private final ServiceContext serviceContext;

    public CommonSecurityAutoConfiguration(final CommonProperties commonProperties,
            final ServiceContext serviceContext) {
        this.commonProperties = commonProperties;
        this.authProperties = commonProperties.getAuth();
        this.serviceContext = serviceContext;
    }

    @PostConstruct
    public void init() {
        log.info("Setting up Common Security Configuration");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        log.info("Setting up Common Security Filter Chain as 'gsshop-common.auth.enabled' is true");
        final RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter(
                jwtHelper(), authProperties.getIgnoreUrls(), jwtTokenEncryptionService());
        final String[] ignoreUrls = Stream.concat(
                authProperties.getSecurityIgnoreDefaultUrls().stream(),
                authProperties.getSecurityIgnoreUrls().stream()).toArray(String[]::new);
        http.authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(ignoreUrls).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler.authenticationEntryPoint(authenticationEntryPoint())
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .addFilterBefore(requestHeaderAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();

    }

    @Bean
    @ConditionalOnProperty(prefix = "gsshop-common.auth", name = "jwt-token-secret")
    public JWTHelper jwtHelper() {
        log.info("Setting up JWT Helper as 'gsshop-common.auth.jwt-token-secret' is set");
        return new JWTHelper(new ObjectMapper(),
                this.commonProperties.getAuth().getJwtTokenSecret());
    }

    @Bean
    @ConditionalOnProperty(prefix = "gsshop-common.auth", name = "jwt-token-secret")
    public JWTTokenEncryptionService jwtTokenEncryptionService() {
        log.info(
                "Setting up JWTTokenEncrpytionService as 'gsshop-common.auth.jwt-token-secret' is set");
        return new JWTTokenEncryptionService(authProperties.getJwtTokenSecret(),
                authProperties.getJwtTokenSalt());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(
                new PreAuthenticatedGrantedAuthoritiesUserDetailsService());
        return provider;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        log.info("Setting up AuthenticationEntryPoint as 'gsshop-common.auth.enabled' is true");
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public MicroservicRestTemplateAuthInterceptor microservicRestTemplateAuthInterceptor() {
        log.info(
                "Setting up MicroservicRestTemplateAuthInterceptor as 'gsshop-common.auth.enabled' is true");
        return new MicroservicRestTemplateAuthInterceptor(serviceContext, jwtHelper(),
                jwtTokenService(), jwtTokenEncryptionService());
    }

    @Bean
    public JwtTokenService jwtTokenService() {
        return new JwtTokenService(authProperties, jwtTokenEncryptionService());
    }

}
