package io.mosip.registration.processor.printing.api.controller.test;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.printing.config.PrintServiceBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;

import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "io.mosip.registration.processor.printing.api.*"}, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				PrintServiceBeanConfig.class, RestConfigBean.class }))
public class PrintServiceConfigTest {

	@MockBean
	public FileSystemAdapter filesystemAdapter;

	@MockBean
	public ConnectionUtils connectionUtil;

	@MockBean
	public TokenValidator tokenValidator;
}
