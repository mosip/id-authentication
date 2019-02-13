package io.mosip.registration.processor.core.kernel.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;

@Configuration
public class KernelConfig {

	@Bean
	@Primary
	public RidValidator<String> getRidValidator(){
		return new RidValidatorImpl();
	}
	@Bean
	public PDFGenerator getPDFGenerator() {
		return new PDFGeneratorImpl();
	}
	
}
