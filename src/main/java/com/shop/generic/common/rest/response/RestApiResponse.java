package com.shop.generic.common.rest.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RestApiResponse<T> {

    private String message;
    private String error;
    private T result;
    private LocalDateTime timestamp;

}
