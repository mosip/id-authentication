package io.mosip.registration.processor.transaction.api.transaction.api.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
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
