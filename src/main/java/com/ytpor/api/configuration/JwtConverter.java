package com.ytpor.api.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    public static final String ROLES = "roles";

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtConverterProperties properties;

    public JwtConverter(JwtConverterProperties properties) {
        this.properties = properties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.of(
                jwtGrantedAuthoritiesConverter.convert(jwt),
                extractResourceRoles(jwt),
                extractRealmRoles(jwt)).flatMap(Collection::stream).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Object resourceAccessObj = jwt.getClaim("resource_access");
        if (!(resourceAccessObj instanceof Map)) {
            return Set.of();
        }

        Map<?, ?> resourceAccess = (Map<?, ?>) resourceAccessObj;
        Object resourceObj = resourceAccess.get(properties.getResourceId());
        if (!(resourceObj instanceof Map)) {
            return Set.of();
        }

        Map<?, ?> resource = (Map<?, ?>) resourceObj;
        Object rolesObj = resource.get(ROLES);
        if (!(rolesObj instanceof Collection)) {
            return Set.of();
        }

        @SuppressWarnings("unchecked")
        Collection<String> resourceRoles = (Collection<String>) rolesObj;

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.get(ROLES) == null) {
            return Set.of();
        }

        Object rolesObj = realmAccess.get(ROLES);
        if (!(rolesObj instanceof Collection<?> roles)) {
            return Set.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
