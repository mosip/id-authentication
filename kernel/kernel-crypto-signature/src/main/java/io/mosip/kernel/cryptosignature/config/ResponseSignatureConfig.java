package io.mosip.kernel.cryptosignature.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "io.mosip.kernel.auth.*" })
public class ResponseSignatureConfig {

}
