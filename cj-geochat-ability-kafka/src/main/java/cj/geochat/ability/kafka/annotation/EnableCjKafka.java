package cj.geochat.ability.kafka.annotation;

import cj.geochat.ability.kafka.config.KafkaConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({KafkaConfig.class})
public @interface EnableCjKafka {
}
