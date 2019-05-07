package io.mosip.admin;

import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.mosip.admin.iddefinition.JsonIdentitySchemaValidator;

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

	@Bean
	public CommandLineRunner runner(JsonIdentitySchemaValidator validator) {
		return args -> {
			String schema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"identity\":{\"title\":\"identity\",\"description\":\"ThisholdsalltheattributesofanIdentity\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"firstName\":{\"$ref\":\"#/definitions/values\"},\"middleName\":{\"$ref\":\"#/definitions/values\"},\"lastName\":{\"$ref\":\"#/definitions/values\"},\"dateOfBirth\":{\"$ref\":\"#/definitions/values\"},\"gender\":{\"$ref\":\"#/definitions/values\"},\"addressLine1\":{\"$ref\":\"#/definitions/values\"},\"addressLine2\":{\"$ref\":\"#/definitions/values\"},\"addressLine3\":{\"$ref\":\"#/definitions/values\"},\"region\":{\"$ref\":\"#/definitions/values\"},\"province\":{\"$ref\":\"#/definitions/values\"},\"city\":{\"$ref\":\"#/definitions/values\"},\"localAdministrativeAuthority\":{\"$ref\":\"#/definitions/values\"},\"mobileNumber\":{\"$ref\":\"#/definitions/values\"},\"emailId\":{\"$ref\":\"#/definitions/values\"},\"CNEOrPINNumber\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianName\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianRIDOrUIN\":{\"$ref\":\"#/definitions/values\"},\"leftEye\":{\"$ref\":\"#/definitions/values\"},\"rightEye\":{\"$ref\":\"#/definitions/values\"},\"biometricScan1\":{\"$ref\":\"#/definitions/values\"},\"biometricScan2\":{\"$ref\":\"#/definitions/values\"},\"biometricScan3\":{\"$ref\":\"#/definitions/values\"}}}},\"definitions\":{\"values\":{\"type\":\"array\",\"additionalItems\":false,\"uniqueItems\":true,\"items\":{\"type\":\"object\",\"required\":[\"language\",\"label\",\"value\"],\"additionalProperties\":false,\"properties\":{\"language\":{\"type\":\"string\"},\"label\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}}}";
			boolean result = validator.validateIdentitySchema(schema);
			System.out.println(result);
		};
	}
}
