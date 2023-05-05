package cj.geochat.ability.oauth2.app.config;

import cj.geochat.ability.oauth2.app.DefaultAppAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public abstract class AppSecurityWorkbin {
    @Bean
    public AuthenticationProvider appAuthenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                authentication.setAuthenticated(true);
                return authentication;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return DefaultAppAuthentication.class.isAssignableFrom(authentication);
            }
        };
    }
}
