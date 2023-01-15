package com.shop.generic.common.rest.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestApiResponse<T> {

    private String message;
    private String error;
    private T result;
    private LocalDateTime timestamp;

}
