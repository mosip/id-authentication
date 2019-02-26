package io.mosip.registration.processor.packet.storage.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtil;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;

@Configuration
@ComponentScan(basePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.auditmanager",
		"io.mosip.registration.processor.rest.client" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				PacketStorageBeanConfig.class, RestConfigBean.class, CoreConfigBean.class, KernelConfig.class }))
public class PacketStorageBeanConfigTest {

	@MockBean
	public FileSystemAdapter filesystemAdapter;

	@MockBean
	public ConnectionUtil connectionUtil;
}
