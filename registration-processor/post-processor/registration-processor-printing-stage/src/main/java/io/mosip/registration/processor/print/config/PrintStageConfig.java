package io.mosip.registration.processor.print.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.registration.processor.print.exception.PrintGlobalExceptionHandler;
import io.mosip.registration.processor.print.stage.PrintStage;

/**
 * @author M1048399
 *
 */
@Configuration
public class PrintStageConfig {

	@Bean 
	public PrintStage getPrintStage() {
		return new PrintStage();
	}
	
	@Bean
	public UinValidator<String> getUinValidator() {
		return new UinValidatorImpl();
	}

	/**
	 * GlobalExceptionHandler bean
	 * 
	 * @return
	 */
	@Bean
	public PrintGlobalExceptionHandler getPrintGlobalExceptionHandler() {
		return new PrintGlobalExceptionHandler();
	}

}
