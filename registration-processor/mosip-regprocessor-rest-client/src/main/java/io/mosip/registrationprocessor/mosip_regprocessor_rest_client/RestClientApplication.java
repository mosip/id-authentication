package io.mosip.registrationprocessor.mosip_regprocessor_rest_client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.audit.builder.AuditLogRequestBuilder;

@SpringBootApplication
@PropertySource({ "classpath:rest-client-application.properties" })
public class RestClientApplication implements CommandLineRunner {
	@Autowired
	private RegistrationProcessorRestClientService<Object> genericRestClient;
	
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;
	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<RegistrationStatusDto> list = new ArrayList<>();
		// Generic GET Client

		//List<RegistrationStatusDto> getResult = (List<RegistrationStatusDto>) genericRestClient.getApi(ApiName.REGSTATUS, "registrationIds","2018782130000224092018121229", list.getClass());
		//System.out.println("Hello  "+getResult);

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
		//List<SyncRegistrationDto> postResult = (List<SyncRegistrationDto>) genericRestClient.postApi(ApiName.REGSYNC,"","", syncRegistrationDto,list.getClass());
		//System.out.println(postResult);

		AuditResponseDto auditresdto=auditLogRequestBuilder.createAuditRequestBuilder("HELLO", "401", "add", "Buisnesss", "Multiple");
System.out.println(auditresdto.isStatus());
	//	System.out.println(postResult);

	}

}
