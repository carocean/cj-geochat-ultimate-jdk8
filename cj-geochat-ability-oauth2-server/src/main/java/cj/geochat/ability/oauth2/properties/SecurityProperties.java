package cj.geochat.ability.oauth2.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("spring.security")
@Setter
@Getter
public class SecurityProperties {
    AuthPageAddress web_auth;
    AuthPageAddress app_auth;
    List<String> whitelist;
    List<String> staticlist;

    public SecurityProperties() {
        whitelist = new ArrayList<>();
        staticlist = new ArrayList<>();
        web_auth = new AuthPageAddress();
        app_auth = new AuthPageAddress();
    }
}
