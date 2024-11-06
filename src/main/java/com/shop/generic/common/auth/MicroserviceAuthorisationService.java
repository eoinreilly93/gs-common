package com.shop.generic.common.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class MicroserviceAuthorisationService {

    public boolean canServiceUpdateOrderStatus() {
        final Permissions permissions = getContext();
        return permissions.isAbleToUpdateOrderStatus();
    }

    public Permissions getContext() {
        return (Permissions) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}
