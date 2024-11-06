package com.shop.generic.common.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shop.generic.common.rest.response.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
public class HttpUtils {

    public static void buildError(final String error, final HttpStatus httpStatus,
            final LocalDateTime dateTime,
            final HttpServletRequest request, final HttpServletResponse httpResponse)
            throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        final RestApiResponse<String> response = new RestApiResponse<>("", error, "", dateTime);
        try {
            httpResponse.getWriter().write(mapper.writeValueAsString(response));
        } catch (final JsonProcessingException e) {
            log.error("An error has occurred in converting error response object", e);
            e.printStackTrace();
        }
        httpResponse.setStatus(httpStatus.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

}
