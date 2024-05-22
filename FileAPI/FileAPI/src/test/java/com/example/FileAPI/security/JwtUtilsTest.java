package com.example.FileAPI.security;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class JwtUtilsTest {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JwtUtils();
        jwtUtils.jwtSecret = jwtSecret;
    }

    @Test
    public void testGenerateJwtToken() {
        String token = jwtUtils.generateJwtToken("testuser");
        assertNotNull(token);

        String username = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        assertEquals("testuser", username);
    }

    @Test
    public void testGetUsernameFromJwtToken() {
        String token = Jwts.builder()
                           .setSubject("testuser")
                           .setIssuedAt(new Date())
                           .setExpiration(new Date((new Date()).getTime() + 3600000))
                           .signWith(SignatureAlgorithm.HS512, jwtSecret)
                           .compact();

        String username = jwtUtils.getUsernameFromJwtToken(token);
        assertEquals("testuser", username);
    }

    @Test
    public void testValidateJwtToken() {
        String token = Jwts.builder()
                           .setSubject("testuser")
                           .setIssuedAt(new Date())
                           .setExpiration(new Date((new Date()).getTime() + 3600000))
                           .signWith(SignatureAlgorithm.HS512, jwtSecret)
                           .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    public void testValidateInvalidJwtToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }
}
