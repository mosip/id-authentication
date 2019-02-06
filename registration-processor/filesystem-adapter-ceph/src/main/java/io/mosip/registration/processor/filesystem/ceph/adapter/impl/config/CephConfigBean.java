package io.mosip.registration.processor.filesystem.ceph.adapter.impl.config;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.ConnectionUtil;

@Configuration
public class CephConfigBean {

	@Bean
	public FileSystemAdapter<InputStream, Boolean> getFilesystemCephAdapter() {
		return new FilesystemCephAdapterImpl(this.getConnectionUtil());
	}
	@Bean
	public ConnectionUtil getConnectionUtil() {
		return new ConnectionUtil();
	}
}
