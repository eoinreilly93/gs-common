package com.shop.generic.common.rest.exceptions;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(final String message) {
        super(message);
    }

    public ServiceUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
