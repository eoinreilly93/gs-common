package com.shop.generic.common.rest.request;

import com.shop.generic.common.rest.exceptions.ServiceException;
import com.shop.generic.common.rest.exceptions.ServiceUnavailableException;
import com.shop.generic.common.rest.exceptions.ValidationException;
import com.shop.generic.common.rest.response.RestApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Utility class for handling all REST requests
 * <p>
 * Logging is handled by the
 * {@link com.shop.generic.common.rest.interceptors.MicroserviceRestTemplateInterceptor}
 */
@Slf4j
public class RestTemplateUtil {

    private final RestTemplate restTemplate;

    public RestTemplateUtil(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> RestApiResponse<T> postForObject(final String url, final Object data,
            final ParameterizedTypeReference<RestApiResponse<T>> typeReference) throws Exception {
        try {
//            log.info("Sending POST request {}", url);
            final HttpEntity<?> requestEntity = new HttpEntity<>(data);
            final ResponseEntity<RestApiResponse<T>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.POST, requestEntity, typeReference);
            return responseEntity.getBody();
        } catch (final ResourceAccessException e) {
            log.error("Error in communicating with API {}", url);
            throw new ServiceUnavailableException(
                    "Request could not be completed because the service is unavailable", e);
        } catch (final HttpClientErrorException e) {
            log.error("Error return by service with api {}", url);
            throw getExceptionForClientError(e);
        }
    }

    public <T> RestApiResponse<T> getForObject(final String url,
            final ParameterizedTypeReference<RestApiResponse<T>> typeReference) throws Exception {
        try {
//            log.info("Sending GET request {}", url);
            final ResponseEntity<RestApiResponse<T>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.GET, null, typeReference);
            return responseEntity.getBody();
        } catch (final ResourceAccessException e) {
            log.error("Error in communicating with API {}", url);
            throw new ServiceUnavailableException(
                    "Request could not be completed because the service is unavailable", e);
        } catch (final HttpClientErrorException e) {
            log.error("Error return by service with api {}", url);
            throw getExceptionForClientError(e);
        }
    }

    public <T> RestApiResponse<T> putForObject(final String url, final Object data,
            final ParameterizedTypeReference<RestApiResponse<T>> typeReference) throws Exception {
        try {
//            log.info("Sending PUT request {}", url);
            final HttpEntity<?> requestEntity = new HttpEntity<>(data);
            final ResponseEntity<RestApiResponse<T>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.PUT, requestEntity, typeReference);
            return responseEntity.getBody();
        } catch (final ResourceAccessException e) {
            log.error("Error in communicating with API {}", url);
            throw new ServiceUnavailableException(
                    "Request could not be completed because the service is unavailable", e);
        } catch (final HttpClientErrorException e) {
            log.error("Error return by service with api {}", url);
            throw getExceptionForClientError(e);
        }
    }

    private Exception getExceptionForClientError(final HttpClientErrorException e)
            throws ValidationException, JSONException {

        final JSONObject jsonObject = new JSONObject(e.getResponseBodyAsString());
        final String errorMessage = (String) jsonObject.get("error");
        final HttpStatusCode statusCode = e.getStatusCode();
        if (statusCode.is4xxClientError()) {
            //throw error relating to user input, such as product not found
            throw new ValidationException(errorMessage);
        } else if (statusCode.is5xxServerError()) {
            throw new ServiceException(errorMessage);
        } else {
            throw new ServiceException(errorMessage);
        }
    }
}
