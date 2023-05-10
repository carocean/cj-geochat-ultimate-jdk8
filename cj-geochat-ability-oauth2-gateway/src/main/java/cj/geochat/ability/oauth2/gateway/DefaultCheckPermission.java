package cj.geochat.ability.oauth2.gateway;

import org.springframework.util.AntPathMatcher;

public class DefaultCheckPermission implements ICheckPermission {
    @Override
    public boolean check(AntPathMatcher antPathMatcher, String role, String accessUrl) {
        return true;
    }
}
