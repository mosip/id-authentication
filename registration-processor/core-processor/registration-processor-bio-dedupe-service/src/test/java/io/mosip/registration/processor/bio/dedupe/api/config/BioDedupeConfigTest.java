package io.mosip.registration.processor.bio.dedupe.api.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;

/**
 * The Class BioDedupeConfig.
 */
@Configuration
@ComponentScan(basePackages = { "io.mosip.registration.processor.bio.dedupe.api.*",
		"io.mosip.registration.processor.packet.storage.*", "io.mosip.registration.processor.rest.client.*",
		"io.mosip.registration.processor.bio.dedupe.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RestConfigBean.class, CoreConfigBean.class, PacketStorageBeanConfig.class, KernelConfig.class }))
public class BioDedupeConfigTest {

	@MockBean
	public FileSystemAdapter filesystemAdapter;

	@MockBean
	public ConnectionUtils connectionUtil;
}
