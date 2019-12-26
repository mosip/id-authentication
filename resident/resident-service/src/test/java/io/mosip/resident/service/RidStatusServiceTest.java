package io.mosip.resident.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.resident.dto.ErrorDTO;
import io.mosip.resident.dto.RegStatusCheckResponseDTO;
import io.mosip.resident.dto.RegistrationStatusDTO;
import io.mosip.resident.dto.RegistrationStatusResponseDTO;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.RIDInvalidException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.impl.ResidentServiceImpl;
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

	private RegistrationStatusResponseDTO responseWrapper;
	private RegistrationStatusDTO response;

	@Before
	public void setup() throws IOException, ApisResourceAccessException {
		requestDTO = new RequestDTO();
		requestDTO.setIndividualId("10006100435989220191202104224");
		requestDTO.setIndividualIdType("RID");

		Mockito.when(env.getProperty(STATUS_CHECK_ID)).thenReturn("id");
		Mockito.when(env.getProperty(STATUS_CHECEK_VERSION)).thenReturn("version");
		Mockito.when(env.getProperty(DATETIME_PATTERN)).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Mockito.when(env.getProperty(REGISTRATIONSTATUSSEARCH)).thenReturn(REGISTRATIONSTATUSSEARCH);

		Mockito.when(ridValidator.validateId(Mockito.anyString())).thenReturn(true);
		responseWrapper = new RegistrationStatusResponseDTO();
		response = new RegistrationStatusDTO();
		response.setRegistrationId("10008100670000320191212101846");
		response.setStatusCode("PROCESSED");
		responseWrapper.setErrors(null);
		responseWrapper.setId("mosip.resident.status");
		responseWrapper.setResponse(response);

		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(responseWrapper);

	}

	@Test
	public void getRidStatusTest() throws ApisResourceAccessException {
		// status PROCESSED
		residentService.getRidStatus(requestDTO);

		// REJECTED
		response.setStatusCode("REJECTED");
		responseWrapper.setResponse(response);
		residentService.getRidStatus(requestDTO);

		// REREGISTER
		response.setStatusCode("REREGISTER");
		responseWrapper.setResponse(response);
		residentService.getRidStatus(requestDTO);

		// RESEND
		response.setStatusCode("RESEND");
		responseWrapper.setResponse(response);
		residentService.getRidStatus(requestDTO);

		// PROCESSING
		response.setStatusCode("PROCESSING");
		responseWrapper.setResponse(response);
		RegStatusCheckResponseDTO result = residentService.getRidStatus(requestDTO);
		assertEquals(result.getRidStatus(), "UNDER PROCESSING - PLEASE CHECK BACK AGAIN LATER.");

	}

	@Test(expected = RIDInvalidException.class)
	public void getRidStatusExceptionTest() throws ApisResourceAccessException {
		try {
			response.setStatusCode("PROCESSED");
			responseWrapper.setResponse(response);
			Mockito.when(ridValidator.validateId(Mockito.anyString())).thenReturn(false);
			residentService.getRidStatus(requestDTO);
		} catch (ResidentServiceException e) {
			Mockito.when(ridValidator.validateId(Mockito.anyString())).thenReturn(true);
		}
		try {
			Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(null);
			residentService.getRidStatus(requestDTO);
		} catch (RIDInvalidException e) {
			Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any()))
					.thenReturn(responseWrapper);
		}
		List<ErrorDTO> errors = new ArrayList<>();
		ErrorDTO error = new ErrorDTO();
		error.setErrorCode("RES-SER-20");
		error.setErrorMessage("Unknown Exception");
		errors.add(error);
		responseWrapper.setErrors(errors);
		try {
			residentService.getRidStatus(requestDTO);
		} catch (RIDInvalidException e) {
			responseWrapper.setErrors(null);
			responseWrapper.setResponse(null);
		}
		residentService.getRidStatus(requestDTO);

	}

	@Test(expected = ResidentServiceException.class)
	public void apiResourceClientExceptionTest() throws ApisResourceAccessException, IOException {
		HttpClientErrorException clientExp = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any()))
				.thenThrow(new ApisResourceAccessException("http client exp", clientExp));
		residentService.getRidStatus(requestDTO);
	}

	@Test(expected = ResidentServiceException.class)
	public void apiResourceServerExceptionTest() throws ApisResourceAccessException, IOException {
		HttpServerErrorException serverExp = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any()))
				.thenThrow(new ApisResourceAccessException("http client exp", serverExp));
		residentService.getRidStatus(requestDTO);
	}

	@Test(expected = ResidentServiceException.class)
	public void apiResourceUnknownExceptionTest() throws ApisResourceAccessException, IOException {
		Mockito.when(residentServiceRestClient.postApi(any(), any(), any(), any(), any()))
				.thenThrow(new ApisResourceAccessException("http client exp", new RuntimeException()));
		residentService.getRidStatus(requestDTO);
	}

	@Test(expected = ResidentServiceException.class)
	public void iOExceptionTest() throws IOException {
		Mockito.when(tokenGenerator.getToken()).thenThrow(new IOException());
		residentService.getRidStatus(requestDTO);

	}

}
