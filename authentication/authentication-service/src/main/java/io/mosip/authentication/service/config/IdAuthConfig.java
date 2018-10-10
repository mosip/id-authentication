package io.mosip.authentication.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = { "classpath:ValidationMessages.properties" })
public class IdAuthConfig {

}
