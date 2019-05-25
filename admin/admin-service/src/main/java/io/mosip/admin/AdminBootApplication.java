package io.mosip.admin;

import java.util.concurrent.Executor;

import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.password.PasswordDetails;
import org.apache.directory.api.util.Base64;
import org.apache.directory.api.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.mosip.admin.masterdata.config.MasterDataCardProperties;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.PasswordUtils;

@SpringBootApplication(scanBasePackages = { "io.mosip.admin.*", "io.mosip.kernel.auth.*" })
@EnableAsync
@EnableConfigurationProperties(MasterDataCardProperties.class)
public class AdminBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminBootApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(40);
		executor.setThreadNamePrefix("Admin-Async-Thread-");
		executor.initialize();
		return executor;
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
             String storedPassword="e1NTSEEyNTZ9TUdiRGxpUHRud1A2Rkp6RDJKK1RvblRmSFlyajl0dVlJOGlhU1ZmWldaWWM4aUsvNW95ZDVnPT0";
             PasswordUtils.compareCredential("mosip", storedPassword);
		};

	}

}
