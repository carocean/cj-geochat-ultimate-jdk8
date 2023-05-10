package cj.geochat.ability.oauth2.asc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IAscResource {
    void login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void authorizeAuthorizationCode(String client_id, String scope, String redirect_uri, String state, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void authorizeImplicit(String client_id, String scope, String redirect_uri, String state, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void confirmAccess(boolean user_oauth_approval, boolean scope_all, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void tokenAuthorizationCode(String code, String client_id, String client_secret, String redirect_uri, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void tokenClientCredentials(String scopes, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void tokenPassword(String username, String password, String client_id, String client_secret, String scopes, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void tokenSmsCode(String phone_num, String sms_code, String client_id, String client_secret, String scopes, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void tokenTenantCode(String tenantid, String access_token, String client_id, String client_secret, String scopes, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void refreshToken(String client_id, String client_secret, String refresh_token, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void checkToken(String token, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void logout(String access_token, HttpServletRequest request, HttpServletResponse response) throws IOException;

    Map<String, Object> getAuthPageAddress() throws IOException;

    List<String> getSupportsGrantTypes() throws IOException;
}
