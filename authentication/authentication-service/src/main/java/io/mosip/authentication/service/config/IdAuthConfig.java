package io.mosip.authentication.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
@PropertySource(value = { "classpath:ValidationMessages.properties" })
public class IdAuthConfig {

}
