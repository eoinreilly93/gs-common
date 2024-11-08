package com.shop.generic.common.rest.response;

import java.time.LocalDateTime;

public class RestApiResponseFactory {

    public <T> RestApiResponse<T> createResponse(final T data, final String message,
            final String error) {
        return RestApiResponse.<T>builder()
                .message(message)
                .error(error)
                .result(data)
                //TODO: Add clock here
                .timestamp(LocalDateTime.now())
                .build();
    }

    public <T> RestApiResponse<T> createSuccessResponse(final T data) {
        return createResponse(data, null, null);
    }

    public <T> RestApiResponse<T> createSuccessResponseWithMessage(final T data,
            final String message) {
        return createResponse(data, message, null);
    }

    public <T> RestApiResponse<T> createErrorResponse(final String error) {
        return createResponse(null, null, error);
    }
}
