package cj.geochat.ability.oauth2.frontapp.annotation;

import cj.geochat.ability.oauth2.frontapp.config.AppResourceServerConfig;
import cj.geochat.ability.swagger.fix.FixNpeForSpringfoxHandlerProviderBeanPostProcessorConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AppResourceServerConfig.class, FixNpeForSpringfoxHandlerProviderBeanPostProcessorConfiguration.class})
//@ConditionalOnWebApplication
public @interface EnableCjOAuth2FrontApp {
}
