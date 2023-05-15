package cj.geochat.ability.oauth2.gateway;

import org.springframework.util.AntPathMatcher;

public class DefaultCheckPermission implements ICheckPermission {
    @Override
    public boolean check(AntPathMatcher antPathMatcher, String username, String role, String resourceIds, String accessUrl) {
        return true;
    }
}
