package io.mosip.registration.processor.quality.checker.config;

import io.mosip.registration.processor.quality.checker.stage.QualityCheckerStage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:bootstrap.properties")
@Configuration
public class QualityCheckerConfig {

    @Bean
    public QualityCheckerStage getStage(){
        return new QualityCheckerStage();
    }
}

