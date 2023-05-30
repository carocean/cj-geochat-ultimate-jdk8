package cj.geochat.ability.oauth2.frontapp.config;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.api.exception.ApiException;
import cj.geochat.ability.oauth2.common.ResultCodeTranslator;
import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthentication;
import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthenticationDetails;
import cj.geochat.ability.oauth2.frontapp.DefaultAppPrincipal;
import cj.geochat.ability.oauth2.userdetails.GeochatUser;
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
import java.util.Map;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"cj.geochat.ability.oauth2.frontapp"})
@ConditionalOnBean({AppSecurityWorkbin.class})
@Slf4j
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired(required = false)
    AuthenticationProvider authenticationProvider;
    @Value("${spring.application.name}")
    String resource_id;
    @Autowired
    TokenStore tokenStore;
    @Autowired
    BearerTokenExtractor tokenExtractor;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        if (StringUtils.hasText(resource_id)) {
            resources.resourceId(resource_id);
        }
        resources.stateless(true)
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCodeTranslator.translateException(authException);
                    Object obj = R.of(rc, authException.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                })
                .tokenStore(tokenStore)
                .tokenExtractor((request) -> {
                    String accessToken = request.getHeader("swagger_token");
                    if (!StringUtils.hasText(accessToken)) {
                        Authentication PreAuthentication = tokenExtractor.extract(request);
                        accessToken = (String) PreAuthentication.getPrincipal();
                    }
                    if (!StringUtils.hasText(accessToken)) {
                        throw new ApiException(ResultCode.INVALID_TOKEN);
                    }
                    OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
                    if (oAuth2Authentication == null) {
                        throw new ApiException(ResultCode.INVALID_TOKEN);
                    }
                    GeochatUser user = (GeochatUser) oAuth2Authentication.getPrincipal();
                    Map<String, String> userDetails = (Map<String, String>) oAuth2Authentication.getUserAuthentication().getDetails();
                    Principal principal = new DefaultAppPrincipal(user.getUsername(), user.getUserId(), userDetails.get("client_id"));
                    DefaultAppAuthenticationDetails details = new DefaultAppAuthenticationDetails(false, request);
                    Authentication authentication = new DefaultAppAuthentication(principal, details, oAuth2Authentication.getUserAuthentication().getAuthorities());
                    return authentication;
                })
                .authenticationManager((authentication ->
                        authenticationProvider.authenticate(authentication)
                ));
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
