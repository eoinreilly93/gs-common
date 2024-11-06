package com.shop.generic.common.auth;

import jakarta.annotation.PostConstruct;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class JWTTokenEncryptionService {

    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";

    private SecretKey secretKey;
    private final String jwtTokenSalt;
    private final String jwtTokenSecret;

    public JWTTokenEncryptionService(final String jwtTokenSecret, final String jwtTokenSalt) {
        this.jwtTokenSecret = jwtTokenSecret;
        this.jwtTokenSalt = jwtTokenSalt;
    }

    @PostConstruct
    public void init() {
        log.info("Initialising JWT Token Encryption Service");
        secretKey = getKeyFromPassword(jwtTokenSecret);
    }

    public String encryptBearerToken(final String bearerToken) {
        String encryptedToken = "";
        if (!StringUtils.isEmpty(bearerToken)) {
            try {
                encryptedToken = encrypt(bearerToken, secretKey);
                log.debug("Bearer Token was encrypted sucessfully");
                return encryptedToken;
            } catch (final InvalidAlgorithmParameterException | InvalidKeyException |
                           BadPaddingException | NoSuchAlgorithmException |
                           IllegalBlockSizeException |
                           NoSuchPaddingException e) {
//                throw new RuntimeException(e);
                log.error("There was an error while encrypting the Bearer Token", e);
            }
        }
        return encryptedToken;
    }

    public String decryptBearerToken(final String encryptedToken) {
        String decryptedToken = "";
        if (!StringUtils.isEmpty(encryptedToken)) {
            try {
                decryptedToken = decrypt(encryptedToken, secretKey);
                log.debug("Bearer Token was decrypted successfully");
                return decryptedToken;
            } catch (final InvalidAlgorithmParameterException | InvalidKeyException |
                           BadPaddingException | NoSuchAlgorithmException |
                           IllegalBlockSizeException |
                           NoSuchPaddingException e) {
//                throw new RuntimeException(e);
                log.error("There was an error while decrypting the Bearer Token", e);
            }
        }
        return decryptedToken;
    }

    private SecretKey getKeyFromPassword(final String password) {
        SecretKey secret = null;
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final KeySpec spec = new PBEKeySpec(password.toCharArray(), jwtTokenSalt.getBytes(),
                    65536, 256);
            secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("There was an error while generating ta key from the jwt token password: ",
                    e);
//            throw new RuntimeException(e);
        }
        return secret;
    }

    private String encrypt(final String input, final SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        final byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    private String decrypt(final String cipherText, final SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        final byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
}
