package io.mosip.resident.testconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackages = { "io.mosip.resident.*", })
public class ResidentServiceConfigTest {

	@Autowired
	private ObjectMapper objectMapper;
	// @MockBean
	// private ResidentService residentService;
}
