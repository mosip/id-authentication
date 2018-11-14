package io.mosip.registrationprocessor.mosip_regprocessor_rest_client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.RestUriConstant;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.service.impl.RegistrationProcessorRestClientServiceImpl;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils.RestApiClient;

@SpringBootApplication
@PropertySource({ "classpath:rest-client-application.properties" })
public class RestClientApplication implements CommandLineRunner {
	@Autowired
	private RegistrationProcessorRestClientService<Object> genericRestClient;

	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		final String getURI = "/v0.1/registration-processor/registration-status/registrationstatus";
		List<RegistrationStatusDto> list = new ArrayList<>();
		// Generic GET Client
		
		List<RegistrationStatusDto> getResult = (List<RegistrationStatusDto>) genericRestClient.getApi(ApiName.AUDIT,new RestUriConstant(getURI), "registrationIds","2018701130000410092018110751", list.getClass());
		System.out.println("Hello  "+getResult);

		final String postURI = "/v0.1/registration-processor/registration-status/sync";
		SyncRegistrationDto dto1 = new SyncRegistrationDto();
		dto1.setIsActive(true);
		dto1.setIsDeleted(false);
		dto1.setLangCode("eng");
		dto1.setParentRegistrationId("fsfdfs45");
		dto1.setRegistrationId("agsg123");
		dto1.setStatusComment("Uploaded to Virus Scanner");
		dto1.setSyncStatus(SyncStatusDto.PRE_SYNC);
		dto1.setSyncType(SyncTypeDto.NEW_REGISTRATION);

		List<SyncRegistrationDto> syncRegistrationDto = new ArrayList<>();
		syncRegistrationDto.add(dto1);
		// Generic POST Client
		List<SyncRegistrationDto> postResult = (List<SyncRegistrationDto>) genericRestClient.postApi(ApiName.AUDIT,new RestUriConstant(postURI),"","", syncRegistrationDto,list.getClass());
		System.out.println(postResult);
	}

}
