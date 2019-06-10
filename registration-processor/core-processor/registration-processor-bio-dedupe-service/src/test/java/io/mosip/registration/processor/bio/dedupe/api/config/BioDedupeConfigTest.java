package io.mosip.registration.processor.bio.dedupe.api.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * The Class BioDedupeConfig.
 */
@Configuration
@ComponentScan(basePackages = { "io.mosip.registration.processor.bio.dedupe.api.controller"},
excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RegistrationStatusBeanConfig.class,RestConfigBean.class, CoreConfigBean.class, PacketStorageBeanConfig.class, KernelConfig.class }))
public class BioDedupeConfigTest {

	@MockBean
	public PacketManager filesystemAdapter;

	@MockBean
	public ConnectionUtils connectionUtil;

	@MockBean
	public RestTemplate restTemplate;
}
