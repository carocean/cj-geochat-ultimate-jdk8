package cj.geochat.ability.oauth2.asc;

import java.io.IOException;

public interface IAuthRemote {
    feign.Response login(String username, String password);

    feign.Response authorize(String response_type, String client_id, String scope, String redirect_uri, String state) throws IOException;


    feign.Response confirmAccess(boolean user_oauth_approval, boolean scope_all) throws IOException;

    feign.Response tokenAuthorizationCode(String grant_type, String code, String client_id, String client_secret, String redirect_uri) throws IOException;

    feign.Response tokenClientCredentials(String grant_type, String scopes) throws IOException;

    feign.Response tokenPassword(String grant_type, String username, String password, String client_id, String client_secret, String scopes) throws IOException;

    feign.Response tokenSmsCode(String grant_type, String phone_num, String sms_code, String client_id, String client_secret, String scopes) throws IOException;

    feign.Response tokenTenantCode(String grant_type, String tenantid, String access_token, String client_id, String client_secret, String scopes) throws IOException;

    feign.Response refreshToken(String grant_type, String client_id, String client_secret, String refresh_token) throws IOException;

    feign.Response checkToken(String token) throws IOException;

    feign.Response logout(String access_token) throws IOException;

    feign.Response getAuthPageAddress() throws IOException;

    feign.Response getSupportsGrantTypes() throws IOException;
}
