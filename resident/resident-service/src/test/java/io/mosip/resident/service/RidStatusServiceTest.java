package io.mosip.resident.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.RegStatusCheckResponseDTO;
import io.mosip.resident.dto.RegistrationStatusDTO;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.RIDInvalidException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.service.NotificationService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

@RunWith(MockitoJUnitRunner.class)
public class RidStatusServiceTest {

	private static final String DATETIME_PATTERN = "mosip.utc-datetime-pattern";
	private static final String STATUS_CHECK_ID = "mosip.resident.service.status.check.id";
	private static final String STATUS_CHECEK_VERSION = "mosip.resident.service.status.check.version";
	private static final String REGISTRATIONSTATUSSEARCH = "REGISTRATIONSTATUSSEARCH";
	@Mock
	ResidentServiceRestClient residentServiceRestClient;
	
	@Mock
	Environment env;
	
	@Mock
	TokenGenerator tokenGenerator;
	
	@Mock
	NotificationService notificationService;
	
	@Mock
	private RidValidator<String> ridValidator;
	
	@InjectMocks
	ResidentServiceImpl residentService = new ResidentServiceImpl();
	
	private RequestDTO requestDTO;
	
	@Before
	public void setup() throws IOException {
		requestDTO = new RequestDTO();
		requestDTO.setIndividualId("10006100435989220191202104224");
		requestDTO.setIndividualIdType("RID");
	
		Mockito.when(env.getProperty(STATUS_CHECK_ID)).thenReturn("id");
		Mockito.when(env.getProperty(STATUS_CHECEK_VERSION)).thenReturn("version");
		Mockito.when(env.getProperty(DATETIME_PATTERN)).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Mockito.when(env.getProperty(REGISTRATIONSTATUSSEARCH)).thenReturn(REGISTRATIONSTATUSSEARCH);
		Mockito.when(ridValidator.validateId(Mockito.anyString())).thenReturn(true);
	}
	
	@Test(expected = ResidentServiceException.class)
	public void testInvalidRID() throws ApisResourceAccessException {
		ResponseWrapper<RegistrationStatusDTO>responseWrapper  = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error = new ServiceError();
		error.setErrorCode("RES-SER-10");
		error.setMessage("Invalid RID");
		errors.add(error);
		responseWrapper.setErrors(errors);
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(responseWrapper);
		requestDTO = new RequestDTO();
		requestDTO.setIndividualId("100061004359892201912021042");
		residentService.getRidStatus(requestDTO);
	}
	
	@Test(expected = ApisResourceAccessException.class)
	public void testRestClientException() throws Exception {

		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(new ApisResourceAccessException());
		residentService.getRidStatus(requestDTO);
	}
	@Test(expected = RIDInvalidException.class)
	public void testInvalidRIDError() throws Exception {
		ResponseWrapper<RegistrationStatusDTO>responseWrapper  = new ResponseWrapper<>();
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(responseWrapper);

		requestDTO.setIndividualId("123456789");
		residentService.getRidStatus(requestDTO);
		
	}
	
	@Test(expected = ResidentServiceException.class)
	public void testError() throws Exception {
		ResponseWrapper<List<LinkedHashMap<String,String>>> responsewrapper = new ResponseWrapper<List<LinkedHashMap<String,String>>>();
		
		ServiceError errorDTO = new ServiceError();
		errorDTO.setErrorCode("code");
		errorDTO.setMessage("message");
		List<ServiceError> errorList = new ArrayList<ServiceError>();
		errorList.add(errorDTO);
		responsewrapper.setErrors(errorList);

		
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(responsewrapper);
		RegStatusCheckResponseDTO response = residentService.getRidStatus(requestDTO);
		assertEquals(response.getRidStatus(), "PROCESSED");
	}
	
	@Test
	public void testRidStatusSuccessCheck() throws Exception {
		ResponseWrapper<List<LinkedHashMap<String,String>>> responsewrapper = new ResponseWrapper<List<LinkedHashMap<String,String>>>();
		List<LinkedHashMap<String,String>> res = new ArrayList<LinkedHashMap<String,String>>();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("registrationId", "10006100435989220191202104224");
		map.put("statusCode", "PROCESSED");
		res.add(map);
		responsewrapper.setResponse(res);
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(responsewrapper);
		NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
		notificationResponseDTO.setMessage("done");
		notificationResponseDTO.setStatus("done");
	//	Mockito.when(notificationService.sendNotification(any())).thenReturn(notificationResponseDTO);
		RegStatusCheckResponseDTO response = residentService.getRidStatus(requestDTO);
		assertEquals(response.getRidStatus(), "PROCESSED");
	}
	
	
}
