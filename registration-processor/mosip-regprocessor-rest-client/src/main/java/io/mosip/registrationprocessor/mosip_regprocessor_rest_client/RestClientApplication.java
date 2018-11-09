package io.mosip.registrationprocessor.mosip_regprocessor_rest_client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto.RequestDetails;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils.RegProcGenericRestClient;

@SpringBootApplication
@PropertySource({ "classpath:rest-client-application.properties" })
public class RestClientApplication
{
	
    public static void main( String[] args )
    {
    	SpringApplication.run(RestClientApplication.class, args);
    }

	/*@Override
	public void run(String... args) throws Exception {
		RegProcGenericRestClient<RegistrationStatusDto,List<RegistrationStatusDto>> reg = new RegProcGenericRestClient();
		
		
SyncRegistrationDto synreg=new SyncRegistrationDto();
		
		//synreg.setIsActive("");
		//synreg.setIsDeleted(isDeleted);
		synreg.setLangCode("en");
		
		synreg.setSyncStatus(SyncStatusDto.PRE_SYNC);
		synreg.setParentRegistrationId("abc123");
		synreg.setStatusComment("new reg");
		synreg.setSyncType(SyncTypeDto.NEW_REGISTRATION);
		synreg.setRegistrationId("abc1");
		
		List<SyncRegistrationDto> list = new ArrayList<>();
		
		list.add(synreg);
		
	ResponseErrorHandler responseHandler=new ResponseErrorHandler() {
			
			@Override
			public boolean hasError(ClientHttpResponse arg0) throws IOException {
			
				return false;
			}
			
			@Override
			public void handleError(ClientHttpResponse arg0) throws IOException {
			
				
			}
		};
		
		String str=	new RegProcGenericRestClient<List, String>().execute(new RequestDetails("http://172.23.30.3:8080/v0.1/registration-processor/registration-status/registrationstatus", HttpMethod.GET),list, responseHandler,String.class);
			
	//String str=	new RegProcGenericRestClient<List<SyncRegistrationDto>, String>().execute(new RequestDetails("http://172.23.30.3:8080/v0.1/registration-processor/registration-status/sync", HttpMethod.POST),list, responseHandler,String.class);
	System.out.println("as my output is :  "+str);		
		//reg.generic("http://172.23.30.3:8080/v0.1/registration-processor/registration-status/registrationstatus?registrationIds=2018782130000224092018121229");

	}*/
}
