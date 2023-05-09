package cj.geochat.ability.oauth2.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

public class CustomAuthenticationWebFilter extends AuthenticationWebFilter {
    IServerAuthenticationFailureHandler serverAuthenticationFailureHandler;

    public CustomAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public CustomAuthenticationWebFilter(ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver) {
        super(authenticationManagerResolver);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String swaggerToken = request.getHeaders().getFirst("swagger_token");
        if (StringUtils.hasText(swaggerToken)) {
            String access_token = request.getQueryParams().getFirst("access_token");
            if (!StringUtils.hasText(access_token)) {
                access_token = request.getHeaders().getFirst("access_token");
            }
            if (!StringUtils.hasText(access_token)) {
                access_token = request.getHeaders().getFirst("Authorization");
            }
            if (!StringUtils.hasText(access_token)) {
                String auth_term = String.format("Bearer %s", swaggerToken);
                request.mutate().header("Authorization", auth_term);
            }
        }
        return super.filter(exchange, chain).onErrorResume(OAuth2Exception.class, (ex) -> {
            return this.serverAuthenticationFailureHandler.onAuthenticationFailure(new WebFilterExchange(exchange, chain), ex);
        });
    }

    public void setServerAuthenticationFailureHandler(IServerAuthenticationFailureHandler serverAuthenticationFailureHandler) {
        this.serverAuthenticationFailureHandler = serverAuthenticationFailureHandler;
    }
}
