package io.mosip.registration.processor.message.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.registration.processor.*")
public class MessageSenderTestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MessageSenderTestApplication.class, args);
	}

}
