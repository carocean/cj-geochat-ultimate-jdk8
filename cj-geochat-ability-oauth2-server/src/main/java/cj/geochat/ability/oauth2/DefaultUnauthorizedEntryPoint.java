package cj.geochat.ability.oauth2;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.oauth2.properties.AuthPageAddress;
import cj.geochat.ability.oauth2.properties.SecurityProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 当未登录而直接访问/oauth/authorize授权码认证端点时会进入此类
 * <pre>告知调用者登录和用户确认页地址，让调用者去跳转。</pre>
 */
public class DefaultUnauthorizedEntryPoint implements AuthenticationEntryPoint {
    SecurityProperties securityProperties;

    public DefaultUnauthorizedEntryPoint(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String queryString = request.getQueryString();
        ResultCode rc = ResultCode.UNAUTHORIZED_CLIENT;
        Map<String, Object> map = new HashMap<>();
        map.put("web_auth", securityProperties.getWeb_auth());
        map.put("app_auth", securityProperties.getApp_auth());
        if (StringUtils.hasText(queryString)) {
            map.put("queryString", queryString);
        }
        Object obj = R.of(rc, map);
        response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
    }
}

