package io.mosip.registration.processor.transaction.api.transaction.api.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;
import io.mosip.registration.processor.status.validator.RegistrationStatusRequestValidator;
import io.mosip.registration.processor.status.validator.RegistrationSyncRequestValidator;

@Configuration
@ComponentScan(basePackages= {"io.mosip.registration.processor.status.service","io.mosip.registration.processor.rest.client.*",
		"io.mosip.registration.processor.core.token.*", "io.mosip.registration.processor.core.config","io.mosip.registration.processor.transaction.*"},
excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {RegistrationStatusBeanConfig.class,
		RestConfigBean.class,RegistrationStatusRequestValidator.class, CoreConfigBean.class, KernelConfig.class}))
public class RegistrationTransactionBeanConfigTest {
	
	@MockBean
	public PacketManager filesystemAdapter;

	@MockBean
	public ConnectionUtils connectionUtil;
	
	@MockBean
	public RegistrationStatusRequestValidator registrationStatusRequestValidator;
	
	@MockBean
	public RegistrationSyncRequestValidator registrationSyncRequestValidator;
}
