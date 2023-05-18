package cj.geochat.ability.oauth2.userdetails;

import cj.geochat.ability.oauth2.IUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class GeochatUser extends User implements IUserDetails {
    private static final long serialVersionUID = 1L;
    String userId;

    public GeochatUser(String userId, String openCode, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(openCode, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId=userId;
    }

    public GeochatUser(String userId, String openCode, String password, Collection<? extends GrantedAuthority> authorities) {
        super(openCode, password, authorities);
        this.userId=userId;
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
