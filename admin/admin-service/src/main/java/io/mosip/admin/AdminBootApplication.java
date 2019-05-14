package io.mosip.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@SpringBootApplication(scanBasePackages = { "io.mosip.admin.*", "io.mosip.kernel.auth.*" })
@EnableAsync
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
	
	public CommandLineRunner runner() {
		return args-> {
			getcurrentTimeStamp();
		};
	}
		
		private String getcurrentTimeStamp() {
			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			System.out.println(LocalDateTime.now(ZoneId.of("UTC")).format(format));
			return LocalDateTime.now(ZoneId.of("UTC")).format(format);
		}

}
