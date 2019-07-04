package io.mosip.registration.processor.abis.handler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:bootstrap.properties")
@Configuration
public class AbisHandlerConfig {

    @Bean
    public AbisHandlerStage getStage(){
        return new AbisHandlerStage();
    }
}
