package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.integration.CreateOTPFrozenExceptionHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateOTPFrozenExceptionHelperConfig {

    @Bean
    public CreateOTPFrozenExceptionHelper createOTPFrozenExceptionHelper() {
        return new CreateOTPFrozenExceptionHelper();
    }
}
