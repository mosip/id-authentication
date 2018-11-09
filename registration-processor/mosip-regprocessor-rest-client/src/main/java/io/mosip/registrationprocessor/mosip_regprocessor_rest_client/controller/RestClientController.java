package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.dto.RequestDetails;
import io.mosip.registrationprocessor.mosip_regprocessor_rest_client.utils.RegProcGenericRestClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/rest-client")
@Api(tags = "Rest-Client")
public class RestClientController<T,V> {

	@PostMapping(path = "/genericrestclient", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "", response = RegistrationStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Officer and Supervisor validated"),
	@ApiResponse(code = 400, message = "Packet already present in landing zone") })
	public V genericRestClient(
			@RequestBody(required = true) T requestdata) {
		
		ResponseErrorHandler responseHandler=new ResponseErrorHandler() {
			
			@Override
			public boolean hasError(ClientHttpResponse arg0) throws IOException {
			
				return false;
			}
			
			@Override
			public void handleError(ClientHttpResponse arg0) throws IOException {
			
				
			}
		};
		
		
		
		//String str=	new RegProcGenericRestClient<T, String>().execute(new RequestDetails("http://172.23.30.3:8080/v0.1/registration-processor/registration-status/registrationstatus", HttpMethod.GET),requestdata, responseHandler,String.class,registrationIds);
		
		String str=	new RegProcGenericRestClient<T, String>().execute(new RequestDetails("http://172.23.30.3:8080/v0.1/registration-processor/registration-status/sync", HttpMethod.POST),requestdata, responseHandler,String.class);
		System.out.println("as my output is :  "+str);		
		return (V) str;
		//return new RegProcGenericRestClient<T, List<SyncRegistrationDto>>().execute(new RequestDetails("URL", HttpMethod.POST),requestdata, responseHandler,RegistrationStatusCode.class);
		//RegProcGenericRestClient<RegistrationStatusDto,List<RegistrationStatusDto>> reg = new RegProcGenericRestClient();
	//	reg.generic("http://localhost:8080/v0.1/registration-processor/registration-status/registrationstatus?registrationIds=2018782130000224092018121229");

	//return new RegProcGenericRestClient<RegistrationStatusDto,List<RegistrationStatusDto>>().generic("http://localhost:8080/v0.1/registration-processor/registration-status/registrationstatus?registrationIds=2018782130000224092018121229");
	//return null;
	}
	
	
	
}
