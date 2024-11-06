package com.shop.generic.common.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JWTTokenEncryptionServiceTest {

    @InjectMocks
    private final JWTTokenEncryptionService jwtTokenEncryptionService = new JWTTokenEncryptionService(
            "3)dgtgh2@4ety", "abcd1234");

    private String bearerToken;

    @BeforeEach
    public void setUp() {
        jwtTokenEncryptionService.init();
        bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJjb250ZXh0IjoiZUp5clZrcE15a2tOeVE4dFNFa3NTZlV2U2trdENpNUpMQ2t0VnJJcUtTcE5yUVVBeWFJTWNRPT0iLCJpYXQiOjE3MzA4MjcxNjgsImV4cCI6MTczMDgzMDc2OH0.sSyiKRNXppEnmgtkkM4KIDf1EmlqP9V9m0PVzG94NIeoeguST4UbdrWXT3y67idqRPl7oTNn_VyrHlTCY8IxUg";
    }

    @DisplayName("Should encrypt the bearer token")
    @Test
    public void should_EncryptToken() {
        final String encryptedToken = jwtTokenEncryptionService.encryptBearerToken(bearerToken);
        assertNotEquals(bearerToken, encryptedToken);
    }

    @DisplayName("Should decrypt the bearer token")
    @Test
    public void should_DecryptToken() {
        final String encryptedToken = jwtTokenEncryptionService.encryptBearerToken(bearerToken);
        assertEquals(bearerToken, jwtTokenEncryptionService.decryptBearerToken(encryptedToken));
    }
}