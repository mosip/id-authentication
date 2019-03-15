package io.mosip.registration.processor.virus.scanner.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.impl.VirusScannerImpl;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.stage.VirusScannerStage;

@Configuration
public class VirusScannerConfig {

	@Bean
	public VirusScannerStage virusScannerStage() {
		return new VirusScannerStage();
	}

	@Bean
	public VirusScanner<Boolean, String> virusScannerService() {
		return new VirusScannerImpl();
	}

	@Bean
	public Decryptor decryptor() {
		return new Decryptor();
	}

	
	@Bean
	public ObjectMapper getObjectMapper() {
	return	new ObjectMapper();
	}

}
