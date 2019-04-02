package io.mosip.registration.processor.core.kernel.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.fsadapter.hdfs.impl.HDFSAdapterImpl;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
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
		return new HDFSAdapterImpl(this.getConnectionUtil());
	}

	@Bean
	@Primary
	public ConnectionUtils getConnectionUtil() {
		return new ConnectionUtils();
	}

	@Bean
	@Primary
	public CbeffUtil getCbeffUtil() {
		return new CbeffImpl();
	}

}
