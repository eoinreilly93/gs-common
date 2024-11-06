package com.shop.generic.common.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.generic.common.properties.AuthProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenService {

    private static final String SEPARATOR = "|";
    private static final String EMPTY = "";
    private final AuthProperties authProperties;
    private final JWTTokenEncryptionService jwtTokenEncryptionService;

    public JwtTokenService(final AuthProperties authProperties,
            final JWTTokenEncryptionService jwtTokenEncryptionService) {
        this.authProperties = authProperties;
        this.jwtTokenEncryptionService = jwtTokenEncryptionService;
    }

    public String generateToken(final boolean canUpdateOrderStatus) {
        final Permissions permissions = new Permissions();
        permissions.setAbleToUpdateOrderStatus(canUpdateOrderStatus);
        log.info("Permissions object has been set in the jwt: {}", permissions);
        return generateJwtToken(permissions);

    }

    private String generateJwtToken(final Permissions permissions) {
        String token = null;
        log.debug("Generating jwt token");
        final String encodedString = Base64.getEncoder()
                .encodeToString(authProperties.getJwtTokenSecret().getBytes());
        try {
            token = Jwts.builder()
                    .setClaims(buildJwtClaims(permissions))
//                    .setSubject(permissions.getUserInfo())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(
                            System.currentTimeMillis() + (authProperties.getJwtTokenValidity())))
                    .signWith(SignatureAlgorithm.HS512, encodedString)
                    .compact();
        } catch (final Exception e) {
//            throw new RuntimeException(e);
            log.error("An error occurred while building the JWT", e);
        }
        log.debug("Generated jwt token, encrypting...");
        final String encryptedToken = jwtTokenEncryptionService.encryptBearerToken(token);
        log.debug("Generated jwt token encrypted");
        return encryptedToken;
    }

    private Map<String, Object> buildJwtClaims(final Permissions permissions) {
        final Map<String, Object> claims = new HashMap<>();
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            final String json = objectMapper.writeValueAsString(permissions);
            log.info("Serialized JSON: {}", json);
            final String payload = compress(objectMapper.writeValueAsBytes(permissions));
            log.info("Encoded (Base64): {} ", payload);

            claims.put("context", payload);
        } catch (final JsonProcessingException e) {
//            throw new RuntimeException(e);
            log.error("Json Parser error: {}", e.getMessage());
        } catch (final IOException e) {
            log.error("Compress error: {}", e.getMessage());
//            throw new RuntimeException(e);
        }
        return claims;
    }

    private String compress(final byte[] data) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
            dos.write(data);
        }
        return new String(Base64.getEncoder().encodeToString(bos.toByteArray()).getBytes(),
                StandardCharsets.UTF_8);
//        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
}
