package cj.geochat.ability.oauth2.frontapp.config;

import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthentication;
import cj.geochat.ability.redis.annotation.EnableCjRedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@EnableCjRedis
@Configuration
public class AppSecurityWorkbin {
    @Bean
    public TokenStore tokenStore(RedisConnectionFactory factory) {
        return new RedisTokenStore(factory);
    }
    @Bean
    public BearerTokenExtractor tokenExtractor() {
        return new BearerTokenExtractor();
    }
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
