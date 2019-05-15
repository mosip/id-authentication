package io.mosip.admin;

import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.configvalidator.ProcessFlowConfigValidator;
import io.mosip.admin.masterdata.config.MasterDataCardProperties;

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
	public CommandLineRunner runner(ProcessFlowConfigValidator service) {
		return args -> {
			service.validateDocumentProcess();
		};
	}
	@Bean
	public RestTemplate template() {
		return new RestTemplate();
	}

}
