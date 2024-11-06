package com.shop.generic.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "gsshop-common")
@Getter
@Setter
public class CommonProperties {

    @NestedConfigurationProperty
    private AuthProperties auth;

}
