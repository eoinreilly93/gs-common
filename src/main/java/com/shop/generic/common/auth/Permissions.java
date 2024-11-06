package com.shop.generic.common.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Very important this class has getters and setters, otherwise Jackson does not serialise it
 * correctly when creating the claims for the JWT, and you will end up with an empty JSON object
 * "{}"
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Permissions {

    private boolean ableToUpdateOrderStatus;

}
