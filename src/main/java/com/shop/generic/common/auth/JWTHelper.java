package com.shop.generic.common.auth;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Base64.getEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.zip.InflaterOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class JWTHelper {

    private final ObjectMapper objectMapper;
    private final String jwtTokenSecret;

    public JWTHelper(final ObjectMapper objectMapper, final String jwtTokenSecret) {
        this.objectMapper = objectMapper;
        this.jwtTokenSecret = jwtTokenSecret;
    }

    public boolean isValidToken(final String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        return !isTokenExpired(token);
    }

    public Permissions getContext(final String token) throws IOException {
        final Claims claims = Jwts.parser()
                .setSigningKey(getEncoder().encodeToString(jwtTokenSecret.getBytes())).build()
                .parseClaimsJws(token).getPayload();
        final String result = (String) claims.get("context");
        log.debug("Encoded (Base64): {} ", result);
        final String payload = deCompress(result);
        log.debug("JWT payload {}", payload);
        return objectMapper.readValue(payload, Permissions.class);
    }

    private String deCompress(final String data) throws IOException {
        final byte[] decode = Base64.getDecoder().decode(data);
        log.debug("Decoded (Base64): {} ", Arrays.toString(decode));
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final OutputStream dos = new InflaterOutputStream(bos)) {
            dos.write(decode);
        }
        return bos.toString(UTF_8);
    }

    private boolean isTokenExpired(final String token) {
        try {
            final Date expiration = getExpirationDate(token);
            return expiration.before(new Date());
        } catch (final ExpiredJwtException e) {
            log.warn("JWT token has expired");
            return true;
        }

    }

    private Date getExpirationDate(final String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(getEncoder().encodeToString(jwtTokenSecret.getBytes())).build()
                .parseClaimsJws(token).getPayload();
        return claimsResolver.apply(claims);
    }
}
