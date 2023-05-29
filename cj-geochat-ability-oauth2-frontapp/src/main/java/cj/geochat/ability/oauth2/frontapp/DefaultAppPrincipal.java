package cj.geochat.ability.oauth2.frontapp;

import org.springframework.util.StringUtils;

import java.security.Principal;

public class DefaultAppPrincipal implements Principal {

    String opencode;
    String userid;
    String appkey;

    public DefaultAppPrincipal() {
    }

    public DefaultAppPrincipal(String opencode,String userid, String appkey) {
        this.opencode = opencode;
        this.userid=userid;
        this.appkey = appkey;
    }


    public String getAppkey() {
        return appkey;
    }

    @Override
    public String getName() {
        return opencode;
    }

    public String getUserid() {
        return userid;
    }

    @Override
    public String toString() {
        String fullName = (StringUtils.hasText(appkey) ? appkey + "::" : "")
                + (StringUtils.hasText(opencode) ? opencode : "")
                +(StringUtils.hasText(userid) ? "."+userid : "")
                ;
        return fullName;
    }

    @Override
    public boolean equals(Object obj) {
        DefaultAppPrincipal other = (DefaultAppPrincipal) obj;
        if (other == null) {
            other = new DefaultAppPrincipal();
        }
        return this.toString().equals(other.toString());
    }
}
