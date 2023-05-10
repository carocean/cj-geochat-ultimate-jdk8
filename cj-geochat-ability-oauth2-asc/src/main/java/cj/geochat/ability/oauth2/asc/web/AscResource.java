package cj.geochat.ability.oauth2.asc.web;

import cj.geochat.ability.api.annotation.ApiResult;
import cj.geochat.ability.oauth2.asc.AbstractResource;
import cj.geochat.ability.oauth2.asc.IAscResource;
import cj.geochat.ability.oauth2.asc.IAuthRemote;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Api(description = "认证服务中心")
public class AscResource extends AbstractResource implements IAscResource {
    @Autowired
    IAuthRemote authRemote;

    @PostMapping("/login")
    @ApiResult
    @ApiOperation("登录")
    @ApiResponses({@ApiResponse(responseCode = "2030", description = "is_authorized")})
    @Override
    public void login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException {
        feign.Response src = authRemote.login(username, password);
        doResponse(src, response);
    }

    @GetMapping("/oauth/authorize/code")
    @ApiResult
    @ApiOperation("授权码模式申请验证码")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void authorizeAuthorizationCode(
            @RequestParam String client_id,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String redirect_uri,
            @RequestParam(required = false) String state,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.authorize("code", client_id, scope, redirect_uri, state);
        doResponse(src, response);
    }

    @GetMapping("/oauth/authorize/token")
    @ApiResult
    @ApiOperation("隐式模式申请令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void authorizeImplicit(
            @RequestParam String client_id,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String redirect_uri,
            @RequestParam(required = false) String state,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.authorize("token", client_id, scope, redirect_uri, state);
        doResponse(src, response);
    }

    @PostMapping("/oauth/confirm_access")
    @ApiResult
    @ApiOperation("用户授权确认")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void confirmAccess(
            @RequestParam boolean user_oauth_approval,
            @RequestParam(required = false) boolean scope_all,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.confirmAccess(user_oauth_approval, scope_all);
        doResponse(src, response);
    }

    @PostMapping("/oauth/token/authorization_code")
    @ApiResult
    @ApiOperation("授权码模式以授权码换取令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void tokenAuthorizationCode(
            @RequestParam String code,
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam String redirect_uri,
            @RequestParam HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.tokenAuthorizationCode("authorization_code", code, client_id, client_secret, redirect_uri);
        doResponse(src, response);
    }

    @PostMapping("/oauth/token/client_credentials")
    @ApiResult
    @ApiOperation("客户端凭证模式获取令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void tokenClientCredentials(
            @RequestParam String grant_type,
            @RequestParam String scopes,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.tokenClientCredentials(grant_type, scopes);
        doResponse(src, response);
    }

    @PostMapping("/oauth/token/password")
    @ApiResult
    @ApiOperation("密码模式获取令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void tokenPassword(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam(required = false) String scopes,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.tokenPassword("password", username, password, client_id, client_secret, scopes);
        doResponse(src, response);
    }

    @PostMapping("/oauth/token/sms_code")
    @ApiResult
    @ApiOperation("短信验证码模式获取令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void tokenSmsCode(
            @RequestParam String phone_num,
            @RequestParam String sms_code,
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam(required = false) String scopes,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.tokenSmsCode("sms_code", phone_num, sms_code, client_id, client_secret, scopes);
        doResponse(src, response);
    }

    @PostMapping("/oauth/token/tenant_code")
    @ApiResult
    @ApiOperation("租户模式获取令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void tokenTenantCode(
            @RequestParam String tenantid,
            @RequestParam String access_token,
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam(required = false) String scopes,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.tokenTenantCode("tenant_code", tenantid, access_token, client_id, client_secret, scopes);
        doResponse(src, response);
    }

    @PostMapping("/oauth/token/refresh_token")
    @ApiResult
    @ApiOperation("刷新令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void refreshToken(
            @RequestParam String client_id,
            @RequestParam String client_secret,
            @RequestParam String refresh_token,
            HttpServletResponse response
    ) throws IOException {
        feign.Response src = authRemote.refreshToken("refresh_token", client_id, client_secret, refresh_token);
        doResponse(src, response);
    }

    @GetMapping("/oauth/token/check_token")
    @ApiResult
    @ApiOperation("检查令牌")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void checkToken(
            @RequestParam String token,
            HttpServletResponse response) throws IOException {
        feign.Response src = authRemote.checkToken(token);
        doResponse(src, response);
    }

    @GetMapping("/logout")
    @ApiResult
    @ApiOperation("登出")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public void logout(@RequestParam String access_token, HttpServletResponse response) throws IOException {
        feign.Response src = authRemote.logout(access_token);
        doResponse(src, response);
    }

    @GetMapping("/auth_page_address")
    @ApiResult
    @ApiOperation("获取认证页面地址，如各端的登录页、用户授权确认页")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public Map<String, Object> getAuthPageAddress(HttpServletResponse response) throws IOException {
        feign.Response src = authRemote.getAuthPageAddress();
        return new ObjectMapper().readValue(readFully(src), HashMap.class);
    }

    @GetMapping("/supports_grant_types")
    @ApiResult
    @ApiOperation("获取支持的授权模式")
    @ApiResponses({@ApiResponse(responseCode = "2000", description = "ok"),
            @ApiResponse(responseCode = "2001", description = "")})
    @Override
    public List<String> getSupportsGrantTypes(HttpServletResponse response) throws IOException {
        feign.Response src = authRemote.getSupportsGrantTypes();
        return new ObjectMapper().readValue(readFully(src), ArrayList.class);
    }


}
