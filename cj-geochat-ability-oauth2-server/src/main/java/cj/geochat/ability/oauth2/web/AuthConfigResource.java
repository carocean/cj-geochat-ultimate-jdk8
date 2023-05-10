package cj.geochat.ability.oauth2.web;

import cj.geochat.ability.api.annotation.ApiResult;
import cj.geochat.ability.oauth2.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class AuthConfigResource {
    @Autowired
    SecurityProperties securityProperties;

    @GetMapping("/auth_page_address")
    @ApiResult
    public Map<String, Object> getAuthPageAddress() {
        Map<String, Object> map = new HashMap<>();
        map.put("app_auth", securityProperties.getApp_auth());
        map.put("web_auth", securityProperties.getWeb_auth());
        return map;
    }

    @GetMapping("/supports_grant_types")
    @ApiResult
    public List<String> supportsGrantTypes() {
        List<String> list = new ArrayList<>();
        list.add("authorization_code");
        list.add("implicit");
        list.add("refresh_token");
        list.add("client_credentials");
        list.add("password");
        list.add("sms_code");
        list.add("tenant_code");
        return list;
    }
}
