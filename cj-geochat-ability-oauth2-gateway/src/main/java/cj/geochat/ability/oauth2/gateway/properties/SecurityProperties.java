package cj.geochat.ability.oauth2.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("spring.security")
@Setter
@Getter
public class SecurityProperties {
    String auth_server;
    List<String> whitelist;
    List<String> staticlist;

    public SecurityProperties() {
        whitelist = new ArrayList<>();
        staticlist = new ArrayList<>();
    }
}
