package cj.geochat.ability.oauth2.config;

import cj.geochat.ability.oauth2.grant.IGrantTypeAuthenticationFactory;
import cj.geochat.ability.oauth2.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.*;

import java.util.List;

/**
 * /oauth/authorize:      专用于授权码模式。两个功能：1、发放授权码（需先登录）；2、检测是否需要用户授权并重定向到授权页然后处理用户向其提交的授权页确认结果。注意：申请授权码的客户端如果开启了用户授权确认，oauth2会发起重定向请求/oauth/confirm_access，因此可拦截此请求返回协议形式，让客户端自定用户确认页面
 * /oauth/token:          获取token（无论是通过授权码或自定义授权模式都要调此接口以换取或获取令牌）
 * /oauth/confirm_access: 用户授权页
 * /oauth/error:          认证失败
 * /oauth/check_token:    资源服务器用来校验token
 * /oauth/token_key:      如果jwt模式则可以用此来从认证服务器获取公钥
 */
@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"cj.geochat.ability.oauth2"})
@ConditionalOnBean({SecurityWorkbin.class})
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    SecurityProperties securityProperties;
    @Autowired(required = false)
    SecurityWorkbin securityWorkbin;
    @Autowired
    AuthenticationSuccessHandler customSuccessAuthentication;
    @Autowired
    AuthenticationFailureHandler customFailureAuthentication;

    @Autowired(required = false)
    IGrantTypeAuthenticationFactory grantTypeAuthenticationFactory;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里配置全局用户信息
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //"/assets/**", "/css/**", "/images/**"
        List<String> staticResources = securityProperties.getStaticlist();
        web.ignoring().antMatchers(staticResources.toArray(new String[0]));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AbstractAuthenticationProcessingFilter filter = securityWorkbin.defaultAuthenticationProcessingFilter(super.authenticationManagerBean(), this.grantTypeAuthenticationFactory);
        filter.setAuthenticationSuccessHandler(customSuccessAuthentication);
        filter.setAuthenticationFailureHandler(customFailureAuthentication);
        http.addFilterAt(filter, UsernamePasswordAuthenticationFilter.class);
        //"/login", "/oauth/**", "/logout"
        List<String> whitelist = securityProperties.getWhitelist();
        http.cors().and().csrf().disable().sessionManagement().disable()
                .exceptionHandling()
                .authenticationEntryPoint(securityWorkbin.unauthorizedEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(whitelist.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(customSuccessAuthentication).failureHandler(customFailureAuthentication)
                .and().headers().frameOptions().disable()
                .and()
                .logout().logoutSuccessHandler(securityWorkbin.logoutSuccessHandler()).clearAuthentication(true).permitAll()
        ;
        List<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> securityConfigs = securityWorkbin.grantTypeAuthenticationFactory().getSecurityConfigs();
        for (SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> c : securityConfigs) {
            http.apply(c);
        }
        //这里引入扩展登陆的配置
//        http.apply(emailSecurityConfigurerAdapter)
//                .and().apply(mobileSecurityConfigurerAdapter)
//                .and().apply(socialSecurityConfigurerAdapter);
    }

}
