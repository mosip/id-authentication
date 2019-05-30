package io.mosip.registration.processor.quality.checker.config;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
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

    @Bean
    public IBioApi getBioApi(){
        return new BioApiImpl();
    }

    @Bean
    public CbeffUtil getCbeffUtil(){
        return new CbeffImpl();
    }
}

