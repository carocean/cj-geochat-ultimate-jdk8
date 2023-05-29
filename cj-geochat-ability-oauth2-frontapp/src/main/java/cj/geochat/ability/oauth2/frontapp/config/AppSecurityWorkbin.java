package cj.geochat.ability.oauth2.frontapp.config;

import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthentication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

public abstract class AppSecurityWorkbin {
    @Bean
    @ConditionalOnMissingBean
    public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory) {
        return new RedisTokenStore(redisConnectionFactory);
    }
    @Bean
    @ConditionalOnMissingBean
    public BearerTokenExtractor bearerTokenExtractor(){
        return new BearerTokenExtractor();
    }
    @Bean
    @ConditionalOnMissingBean
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
