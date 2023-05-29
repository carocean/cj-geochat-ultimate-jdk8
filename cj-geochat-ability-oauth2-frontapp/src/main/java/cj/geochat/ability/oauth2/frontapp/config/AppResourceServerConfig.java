package cj.geochat.ability.oauth2.frontapp.config;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.oauth2.common.ResultCodeTranslator;
import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthentication;
import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthenticationDetails;
import cj.geochat.ability.oauth2.frontapp.DefaultAppPrincipal;
import cj.geochat.ability.oauth2.frontapp.DefaultTenantPrincipal;
import cj.geochat.ability.redis.annotation.EnableCjRedis;
import cj.geochat.ability.redis.config.RedisConfig;
import cj.geochat.ability.util.GeochatRuntimeException;
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
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableCjRedis
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"cj.geochat.ability.oauth2.frontapp"})
@ConditionalOnBean({RedisConfig.class, AppSecurityWorkbin.class})
@Slf4j
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired(required = false)
    AuthenticationProvider authenticationProvider;
    @Value("${spring.application.name}")
    String resource_id;
    @Autowired
    TokenStore tokenStore;
    @Autowired(required = false)
    BearerTokenExtractor bearerTokenExtractor;

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
                    String swaggerToken = request.getHeader("swagger_token");
                    if (StringUtils.hasText(swaggerToken)) {
                        return extractSwaggerToken(swaggerToken, false, request);
                    }

                    Authentication PreAuthentication = bearerTokenExtractor.extract(request);
                    String accessToken = (String) PreAuthentication.getPrincipal();
                    OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);

//                    Principal principal =  new DefaultAppPrincipal(opencode, userid, appkey);
//                    DefaultAppAuthenticationDetails details = new DefaultAppAuthenticationDetails(isBoolFromGateway, request);
//                    Authentication authentication = new DefaultAppAuthentication(principal, details, authorityList);
//                    return authentication;
                    return oAuth2Authentication;
                })
                .authenticationManager((authentication ->
                        authenticationProvider.authenticate(authentication)
                ));
    }

    private Authentication extractSwaggerToken(String swaggerToken, boolean isFromGateway, HttpServletRequest request) {
        //租户标识::应用标识::登录账号.用户标识::角色1,角色2
        String[] terms = swaggerToken.split("::");
        if (terms.length != 4 && terms.length != 3) {
            String err = "swagger_token格式不正确，抽取令牌过程被中止，正确格式：租户标识::应用标识::用户::角色1,角色2，如果某项为空但::分隔不能少";
            log.warn(err);
            throw new GeochatRuntimeException("5001", err);
        }
        String user = terms[2];
        if (!StringUtils.hasText(user)) {
            throw new GeochatRuntimeException("5000", "swagger_token is not contain a user.");
        }
        int pos = user.lastIndexOf(".");
        String opencode = "";
        String userid = "";
        if (pos < 0) {
            opencode = user;
        } else {
            opencode = user.substring(0, pos);
            userid = user.substring(pos + 1, user.length());
        }
        String appkey = terms[1];
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
        Principal principal = StringUtils.hasText(tenantid) ? new DefaultTenantPrincipal(opencode, userid, appkey, tenantid) : new DefaultAppPrincipal(opencode, userid, appkey);
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
