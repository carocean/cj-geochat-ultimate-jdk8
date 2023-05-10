package cj.geochat.ability.oauth2.asc;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IAscResource {
    void login(String username, String password, HttpServletResponse response) throws IOException;

    void authorizeAuthorizationCode(String client_id, String scope, String redirect_uri, String state, HttpServletResponse response) throws IOException;

    void authorizeImplicit(String client_id, String scope, String redirect_uri, String state, HttpServletResponse response) throws IOException;

    void confirmAccess(boolean user_oauth_approval, boolean scope_all, HttpServletResponse response) throws IOException;

    void tokenAuthorizationCode(String code, String client_id, String client_secret, String redirect_uri, HttpServletResponse response) throws IOException;

    void tokenClientCredentials(String scopes, HttpServletResponse response) throws IOException;

    void tokenPassword(String username, String password, String client_id, String client_secret, String scopes, HttpServletResponse response) throws IOException;

    void tokenSmsCode(String phone_num, String sms_code, String client_id, String client_secret, String scopes, HttpServletResponse response) throws IOException;

    void tokenTenantCode(String tenantid, String access_token, String client_id, String client_secret, String scopes, HttpServletResponse response) throws IOException;

    void refreshToken(String client_id, String client_secret, String refresh_token, HttpServletResponse response) throws IOException;

    void checkToken(String token, HttpServletResponse response) throws IOException;

    void logout(String access_token, HttpServletResponse response) throws IOException;

    Map<String, Object> getAuthPageAddress(HttpServletResponse response) throws IOException;

    List<String> getSupportsGrantTypes(HttpServletResponse response) throws IOException;
}
