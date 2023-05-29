package cj.geochat.ability.oauth2.frontapp;

import org.springframework.util.StringUtils;

public class DefaultTenantPrincipal extends DefaultAppPrincipal {
    String tenantid;

    public DefaultTenantPrincipal() {
    }

    public DefaultTenantPrincipal(String opencode,String userid, String appkey, String tenantid) {
        super(opencode,userid, appkey);
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
