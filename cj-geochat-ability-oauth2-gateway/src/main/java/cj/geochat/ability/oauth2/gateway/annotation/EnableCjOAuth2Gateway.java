package cj.geochat.ability.oauth2.gateway.annotation;

import cj.geochat.ability.oauth2.gateway.web.rest.DefaultTokenResource;
import cj.geochat.ability.oauth2.gateway.config.ResourceServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ResourceServerConfig.class, DefaultTokenResource.class})
//@ConditionalOnWebApplication
public @interface EnableCjOAuth2Gateway {
}
