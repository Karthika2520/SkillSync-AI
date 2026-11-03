package com.skillsync.backend.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;



@Configuration 
public class JwtConfig 
{
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey secretKey()
    {
        byte[] decodedKey=Base64.getDecoder().decode(jwtSecret);
        if(decodedKey.length<32)
        {
            throw new IllegalArgumentException("JWT secret must contain atleast 32 bytes");
        }
        return new SecretKeySpec(decodedKey,"HmacSHA256");
    }
    
    @Bean 
    public JwtEncoder jwtEncoder(SecretKey secretKey)
    {
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey));
    }

    @Bean 
    public JwtDecoder JwtDecoder(SecretKey secretKey)
    {
        return NimbusJwtDecoder
        .withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
    }
    

}
