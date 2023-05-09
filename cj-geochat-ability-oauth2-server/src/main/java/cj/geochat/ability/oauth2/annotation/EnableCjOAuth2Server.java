package cj.geochat.ability.oauth2.annotation;

import cj.geochat.ability.oauth2.config.AuthorizationServerConfig;
import cj.geochat.ability.oauth2.config.WebSecurityConfig;
import cj.geochat.ability.oauth2.web.AuthConfigResource;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AuthorizationServerConfig.class, WebSecurityConfig.class, AuthConfigResource.class})
//@ConditionalOnWebApplication
public @interface EnableCjOAuth2Server {
}
