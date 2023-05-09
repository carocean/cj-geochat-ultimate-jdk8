package cj.geochat.ability.oauth2;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.oauth2.redis.ITenantStore;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 当调用登出地址时会进入此类
 */
@Component
@Slf4j
public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    ITenantStore tenantStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String accessToken = request.getParameter("access_token");
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getHeader("access_token");
        }
        if (StringUtils.isNotBlank(accessToken)) {
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
            if (oAuth2AccessToken != null) {
                OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
                if (auth2Authentication != null) {
                    Map<String, Object> details = (Map<String, Object>) auth2Authentication.getDetails();
                    if (details != null && details.containsKey("tenantid")) {
                        String name = auth2Authentication.getName();
                        Authentication innerAuth = auth2Authentication.getUserAuthentication();
                        if (innerAuth != null) {
                            Map<String, Object> innerDetails = (Map<String, Object>) innerAuth.getDetails();
                            if (innerDetails != null && innerDetails.containsKey("client_id")) {
                                String clientId = (String) innerDetails.get("client_id");
                                tenantStore.remoteTenant(name, clientId);
                            }
                        }

                    }
                }
                log.debug("注册的access_token是：" + oAuth2AccessToken.getValue());
                tokenStore.removeAccessToken(oAuth2AccessToken);
                OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
                tokenStore.removeRefreshToken(oAuth2RefreshToken);
                tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ResultCode rc = ResultCode.IS_LOGOUT;
                Object obj = R.of(rc, "ok");
                response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                return;
            }
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResultCode rc = ResultCode.IS_LOGOUT_FAILURE;
        Object obj = R.of(rc, "access_token is empty or not in store.");
        response.getWriter().write(new ObjectMapper().writeValueAsString(obj));

    }
}
