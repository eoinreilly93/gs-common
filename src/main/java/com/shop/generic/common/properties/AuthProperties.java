package com.shop.generic.common.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthProperties {

    private boolean enabled = false; //disabled by default
    private String jwtTokenSecret;
    private String jwtTokenSalt;
    private long jwtTokenValidity;
    private List<String> securityIgnoreDefaultUrls = Arrays.asList(
            "/component-test/**",
            "/h2/",
            "/actuator/**",
            "/error/**"
    );
    private List<String> securityIgnoreUrls = new ArrayList<>();


    public String[] getIgnoreUrls() {
        return Stream.concat(securityIgnoreDefaultUrls.stream(), securityIgnoreUrls.stream())
                .toArray(String[]::new);
    }
}
