package cj.geochat.ability.oauth2.app;

import org.springframework.util.StringUtils;

import java.security.Principal;

public class DefaultTenantPrincipal extends DefaultAppPrincipal {
    String tenantid;

    public DefaultTenantPrincipal() {
    }

    public DefaultTenantPrincipal(String user, String appid, String tenantid) {
        super(user, appid);
        this.tenantid = tenantid;
    }

    public String getTenantid() {
        return tenantid;
    }

    @Override
    public String toString() {
        String fullName = (StringUtils.hasText(tenantid) ? tenantid + "::" : "")
                + super.toString();
        return fullName;
    }

    @Override
    public boolean equals(Object obj) {
        DefaultTenantPrincipal other = (DefaultTenantPrincipal) obj;
        if (other == null) {
            other = new DefaultTenantPrincipal();
        }
        return this.toString().equals(other.toString());
    }
}
