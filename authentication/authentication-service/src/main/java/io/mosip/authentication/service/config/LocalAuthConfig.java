package io.mosip.authentication.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("local")
@PropertySource("classpath:ValidationMessages.properties")
public class LocalAuthConfig {

}
