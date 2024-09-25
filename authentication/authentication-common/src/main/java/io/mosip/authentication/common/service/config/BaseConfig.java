package io.mosip.authentication.common.service.config;

import io.mosip.kernel.websub.api.config.WebSubClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
@author Kamesh Shekhar Prasad
 */

@Configuration
@Import(WebSubClientConfig.class)
public class BaseConfig {

}
