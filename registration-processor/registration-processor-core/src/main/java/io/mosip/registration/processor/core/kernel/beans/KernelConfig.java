package io.mosip.registration.processor.core.kernel.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.fsadapter.hdfs.impl.HDFSAdapter;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtil;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;

@Configuration
public class KernelConfig {

	@Bean
	@Primary
	public RidValidator<String> getRidValidator() {
		return new RidValidatorImpl();
	}

	@Bean
	@Primary
	public FileSystemAdapter getFileSystemAdapter() {
		return new HDFSAdapter(this.getConnectionUtil());
	}

	@Bean
	@Primary
	public ConnectionUtil getConnectionUtil() {
		return new ConnectionUtil();
	}
}
