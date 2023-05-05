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
    AuthWebInfo auth_web;
    AuthServerInfo auth_server;
    List<String> whitelist;
    List<String> static_resources;
    List<ClientInfo> clients;
    public SecurityProperties() {
        whitelist = new ArrayList<>();
        static_resources = new ArrayList<>();
        auth_web = new AuthWebInfo();
        clients = new ArrayList<>();
    }
}
