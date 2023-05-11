package cj.geochat.ability.oauth2.asc.annotation;

import cj.geochat.ability.oauth2.asc.config.FeignClientsConfigurationCustom;
import cj.geochat.ability.oauth2.asc.web.AscResource;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AscResource.class, FeignClientsConfigurationCustom.class})
public @interface EnableCjAsc {
}
