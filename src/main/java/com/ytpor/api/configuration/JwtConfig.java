package com.ytpor.api.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    private final ApplicationProperties applicationProperties;

    public JwtConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        ApplicationProperties.JWT jwt = applicationProperties.getJwt();
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwt.getKey().getBytes()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        ApplicationProperties.JWT jwt = applicationProperties.getJwt();
        byte[] bytes = jwt.getKey().getBytes();
        SecretKeySpec originalKey = new SecretKeySpec(bytes, 0, bytes.length, "RSA");
        return NimbusJwtDecoder.withSecretKey(originalKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
