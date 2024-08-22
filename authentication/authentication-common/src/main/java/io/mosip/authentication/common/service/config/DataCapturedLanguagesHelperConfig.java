package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.DataCapturedLanguagesHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataCapturedLanguagesHelperConfig {

    @Bean
    public DataCapturedLanguagesHelper dataCapturedLanguagesHelper() {
        return new DataCapturedLanguagesHelper();
    }
}
