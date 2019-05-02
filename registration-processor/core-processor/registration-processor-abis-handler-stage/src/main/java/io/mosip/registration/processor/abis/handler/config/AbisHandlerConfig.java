package io.mosip.registration.processor.abis.handler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;

@Configuration
public class AbisHandlerConfig {

    @Bean
    public AbisHandlerStage getStage(){
        return new AbisHandlerStage();
    }
}
