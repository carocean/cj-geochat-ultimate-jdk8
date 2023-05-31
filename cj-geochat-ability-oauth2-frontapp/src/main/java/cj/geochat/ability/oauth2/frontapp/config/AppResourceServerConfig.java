package cj.geochat.ability.oauth2.frontapp.config;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.oauth2.common.ResultCodeTranslator;
import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthentication;
import cj.geochat.ability.oauth2.frontapp.DefaultAppAuthenticationDetails;
import cj.geochat.ability.oauth2.frontapp.DefaultAppPrincipal;
import cj.geochat.ability.oauth2.frontapp.properties.SecurityProperties;
import cj.geochat.ability.oauth2.userdetails.GeochatUser;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnBean({AppSecurityWorkbin.class})
@Slf4j
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter implements InitializingBean {
    @Autowired(required = false)
    AuthenticationProvider authenticationProvider;
    @Value("${spring.application.name}")
    String resource_id;
    @Autowired
    TokenStore tokenStore;
    @Autowired
    BearerTokenExtractor tokenExtractor;
    @Autowired
    SecurityProperties securityProperties;
    AntPathMatcher antPathMatcher;
    List<String> allWhitelist;

    @Override
    public void afterPropertiesSet() throws Exception {
        antPathMatcher = new AntPathMatcher();
        List<String> whitelist = securityProperties.getWhitelist();
        List<String> staticResources = securityProperties.getStaticlist();
        allWhitelist = new ArrayList<>();
        allWhitelist.addAll(whitelist);
        allWhitelist.addAll(staticResources);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        if (StringUtils.hasText(resource_id)) {
            resources.resourceId(resource_id);
        }
        OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler = new OAuth2AccessDeniedHandler();
        oAuth2AccessDeniedHandler.setExceptionTranslator(e -> {
            OAuth2Exception exception = (OAuth2Exception) e;
            String errorCode = exception.getOAuth2ErrorCode();
            ResultCode rc = ResultCodeTranslator.translateResultCode(errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/json;charset=utf-8")
                    .body(R.of(rc, e.getMessage()));
        });
        resources.stateless(true)
                .accessDeniedHandler((request, response, e) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCode.ACCESS_DENIED;
                    Object r = R.of(rc, e.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(r));
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCodeTranslator.translateException(authException);
                    Object obj = R.of(rc, authException.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                })
                .tokenStore(tokenStore)
                .tokenExtractor((request) -> {
                    boolean match = allWhitelist.stream().anyMatch(url -> antPathMatcher.match(url, request.getRequestURI()));
                    if (match) {
                        return null;
                    }
                    String accessToken = request.getHeader("swagger_token");
                    if (!StringUtils.hasText(accessToken)) {
                        Authentication PreAuthentication = tokenExtractor.extract(request);
                        if (PreAuthentication == null) {
                            throw new InvalidTokenException("No access_token found in request header");
                        }
                        accessToken = (String) PreAuthentication.getPrincipal();
                    }
                    if (!StringUtils.hasText(accessToken)) {
                        throw new InvalidTokenException("access_token required");
                    }
                    OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
                    if (oAuth2Authentication == null) {
                        throw new InvalidTokenException("access_token is invalid or has expired");
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
        http.cors().and().csrf().disable().logout().disable().formLogin().disable()
                .authorizeRequests()
                .antMatchers(allWhitelist.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider)
        ;
    }

}
