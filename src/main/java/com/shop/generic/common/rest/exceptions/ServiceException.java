package com.shop.generic.common.rest.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(final String message) {
        super(message);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
