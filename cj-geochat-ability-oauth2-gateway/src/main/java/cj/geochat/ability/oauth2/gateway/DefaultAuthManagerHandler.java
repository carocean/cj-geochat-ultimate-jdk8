package cj.geochat.ability.oauth2.gateway;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultAuthManagerHandler implements ReactiveAuthorizationManager<AuthorizationContext> {
    ICheckPermission checkPermission;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public DefaultAuthManagerHandler(ICheckPermission checkPermission) {
        this.checkPermission = checkPermission;
    }

    //自定义地址权限拦截实现
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        ServerHttpRequest request = object.getExchange().getRequest();
        String requestUrl = request.getPath().pathWithinApplication().value();
        // 1. 对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        return authentication
                .filter(a -> a.isAuthenticated())
                .flatMapIterable(a -> a.getAuthorities().stream().flatMap(e -> {
                    Set<String> ids = (a instanceof OAuth2Authentication) ? ((OAuth2Authentication) a).getOAuth2Request().getResourceIds() : new HashSet<>();
                    List<InnerObject> list = new ArrayList<>();
                    if (ids == null || ids.isEmpty()) {
                        InnerObject innerObject = new InnerObject();
                        innerObject.authority = e.getAuthority();
                        innerObject.username = a.getName();
                        innerObject.resourceId = null;
                        list.add(innerObject);
                    } else {
                        ids.forEach(z -> {
                            InnerObject innerObject = new InnerObject();
                            innerObject.authority = e.getAuthority();
                            innerObject.username = a.getName();
                            innerObject.resourceId = z;
                            list.add(innerObject);
                        });
                    }
                    return list.stream();
                }).collect(Collectors.toList()))
                .any(c -> {
                    if (checkPermission != null) {
                        return checkPermission.check(antPathMatcher, c.username, String.valueOf(c.authority), c.resourceId, requestUrl);
                    }
                    return true;
                })
                .map(hasAuthority -> new AuthorizationDecision(hasAuthority))
                .defaultIfEmpty(new AuthorizationDecision(true));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return null;
    }

    class InnerObject {
        String authority;
        String username;
        String resourceId;
    }
}


