package cj.geochat.ability.oauth2.app.config;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.oauth2.app.DefaultAppAuthentication;
import cj.geochat.ability.oauth2.app.DefaultAppAuthenticationDetails;
import cj.geochat.ability.oauth2.app.DefaultTenantPrincipal;
import cj.geochat.ability.oauth2.app.DefaultAppPrincipal;
import cj.geochat.ability.oauth2.common.ResultCodeTranslator;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"cj.geochat.ability.oauth2.app"})
@ConditionalOnBean({AppSecurityWorkbin.class})
@Slf4j
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired(required = false)
    AuthenticationProvider authenticationProvider;
    @Value("${spring.application.name}")
    String resource_id;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        if (StringUtils.hasText(resource_id)) {
            resources.resourceId(resource_id);
        }
        resources.stateless(false)
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCodeTranslator.translateException(authException);
                    Object obj = R.of(rc, authException.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                }).tokenExtractor((request) -> {
                    String isFromGateway = request.getHeader("x-from-gateway");
                    boolean isBoolFromGateway = "true".equals(isFromGateway);
                    //如果不是来自网关的swagger调用才进入以下处理。
                    //因为在网关中已对swagger的调用解析了，并将swagger_token附到了请求的header中供网关解析
                    if (!isBoolFromGateway) {
                        String swaggerToken = request.getHeader("swagger_token");
                        if (StringUtils.hasText(swaggerToken)) {
                            return extractSwaggerToken(swaggerToken, isBoolFromGateway, request);
                        }
                    }
                    String user = request.getHeader("x-user");
                    if (!StringUtils.hasText(user)) {//这说明是内部访问（不是经过网关的请求），改为匿名访问，除非使用swagger_token。
                        Principal principal = new DefaultAppPrincipal("anonymous_user", "anonymous_appid");
                        DefaultAppAuthenticationDetails details = new DefaultAppAuthenticationDetails(isBoolFromGateway, request);
                        Authentication authentication = new DefaultAppAuthentication(principal, details, new ArrayList<>());
                        return authentication;
                    }
                    String appid = request.getHeader("x-appid");
                    List<GrantedAuthority> authorityList = new ArrayList<>();
                    String roles = request.getHeader("x-roles");
                    if (StringUtils.hasText(roles)) {
                        String roleArr[] = roles.split(",");
                        for (String role : roleArr) {
                            authorityList.add(new SimpleGrantedAuthority(role));
                        }
                    }
                    String tenantid = request.getHeader("x-tenantid");
                    Principal principal = StringUtils.hasText(tenantid) ? new DefaultTenantPrincipal(user, appid, tenantid) : new DefaultAppPrincipal(user, appid);
                    DefaultAppAuthenticationDetails details = new DefaultAppAuthenticationDetails(isBoolFromGateway, request);
                    Authentication authentication = new DefaultAppAuthentication(principal, details, authorityList);
                    return authentication;
                })
                .authenticationManager((authentication ->
                        authenticationProvider.authenticate(authentication)
                ));
    }

    private Authentication extractSwaggerToken(String swaggerToken, boolean isFromGateway, HttpServletRequest request) {
        //租户标识::应用标识::用户::角色1,角色2
        String[] terms = swaggerToken.split("::");
        if (terms.length != 4 && terms.length != 3) {
            String err = "swagger_token格式不正确，抽取令牌过程被中止，正确格式：租户标识::应用标识::用户::角色1,角色2，如果某项为空但::分隔不能少";
            log.warn(err);
            throw new RuntimeException(err);
        }
        String user = terms[2];
        if (!StringUtils.hasText(user)) {
            throw new RuntimeException("swagger_token is not contain a user.");
        }
        String appid = terms[1];
        List<GrantedAuthority> authorityList = new ArrayList<>();

        if (terms.length == 4) {
            String roles = terms[3];
            if (StringUtils.hasText(roles)) {
                String roleArr[] = roles.split(",");
                for (String role : roleArr) {
                    authorityList.add(new SimpleGrantedAuthority(role));
                }
            }
        }
        String tenantid = terms[0];
        Principal principal = StringUtils.hasText(tenantid) ? new DefaultTenantPrincipal(user, appid, tenantid) : new DefaultAppPrincipal(user, appid);
        DefaultAppAuthenticationDetails details = new DefaultAppAuthenticationDetails(isFromGateway, request);
        Authentication authentication = new DefaultAppAuthentication(principal, details, authorityList);
        return authentication;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().logout().disable().formLogin().disable().anonymous().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling()
                .accessDeniedHandler(((request, response, e) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCode.ACCESS_DENIED;
                    Object r = R.of(rc, e.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(r));
                }))
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCodeTranslator.translateException(authException);
                    Object obj = R.of(rc, authException.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                })
                .and()
                .authenticationProvider(authenticationProvider)
        ;
    }

}
