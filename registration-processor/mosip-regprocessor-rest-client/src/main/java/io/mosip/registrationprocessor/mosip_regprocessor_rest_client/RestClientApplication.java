package io.mosip.registrationprocessor.mosip_regprocessor_rest_client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils.GenericRestClient;

@SpringBootApplication
@PropertySource({ "classpath:rest-client-application.properties" })
public class RestClientApplication implements CommandLineRunner {
@Autowired
private GenericRestClient genericRestClient;
	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		final String getURI = "http://localhost:8080/v0.1/registration-processor/registration-status/registrationstatus";
		List<RegistrationStatusDto> list = new ArrayList<>();
		// Generic GET Client
		List<RegistrationStatusDto> getResult = genericRestClient.genericGETClient(getURI, "registrationIds",
				"2018782130000224092018121229", list.getClass());
		System.out.println(getResult);

		final String postURI = "http://localhost:8080/v0.1/registration-processor/registration-status/sync";
		SyncRegistrationDto dto1 = new SyncRegistrationDto();
		dto1.setIsActive(true);
		dto1.setIsDeleted(false);
		dto1.setLangCode("eng");
		dto1.setParentRegistrationId("");
		dto1.setRegistrationId("2018782130000224092018121229");
		dto1.setStatusComment("Uploaded to Virus Scanner");
		dto1.setSyncStatus(SyncStatusDto.PRE_SYNC);
		dto1.setSyncType(SyncTypeDto.NEW_REGISTRATION);

		List<SyncRegistrationDto> syncRegistrationDto = new ArrayList<>();
		syncRegistrationDto.add(dto1);
		// Generic POST Client
		List<SyncRegistrationDto> postResult = genericRestClient.genericPostClient(postURI, syncRegistrationDto,syncRegistrationDto.getClass());
		System.out.println(postResult);
	}

}
