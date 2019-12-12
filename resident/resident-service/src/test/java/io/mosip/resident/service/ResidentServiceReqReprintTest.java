package io.mosip.resident.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.RegProcRePrintResponseDto;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResidentReprintResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.util.NotificationService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

@RunWith(SpringRunner.class)
public class ResidentServiceReqReprintTest {
	@InjectMocks
	ResidentServiceImpl residentServiceImpl;

	@Mock
	private ResidentServiceRestClient residentServiceRestClient;

	@Mock
	private IdAuthService idAuthService;

	@Mock
	private UinValidator<String> uinValidator;

	@Mock
	Environment env;

	@Mock
	private TokenGenerator tokenGenerator;

	@Mock
	NotificationService notificationService;

	private ResidentReprintRequestDto residentReqDto;

	@Before
	public void setUp() throws IOException, OtpValidationFailedException {
		Mockito.when(env.getProperty(ApiName.REPRINTUIN.name()))
				.thenReturn("https://int.mosip.io/registrationprocessor/v1/requesthandler/reprint");
		Mockito.when(tokenGenerator.getRegprocToken()).thenReturn("assagfdhsfiuhewqedsavckdsann");
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(true);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		residentReqDto = new ResidentReprintRequestDto();
		residentReqDto.setIndividualId("3527812406");
		residentReqDto.setIndividualIdType(IdType.UIN);
		residentReqDto.setOtp("689745");
		residentReqDto.setTransactionID("0987654321");

	}

	@Test
	public void reqPrintUinTest() throws ApisResourceAccessException, OtpValidationFailedException, IOException,
			ResidentServiceCheckedException {

		ResponseWrapper<RegProcRePrintResponseDto> response = new ResponseWrapper<>();
		RegProcRePrintResponseDto reprintResp = new RegProcRePrintResponseDto();
		reprintResp.setMessage("sent to packet receiver");
		reprintResp.setRegistrationId("10008200070004620191203115734");
		reprintResp.setStatus("success");
		response.setResponse(reprintResp);
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(response);
		NotificationResponseDTO notificationResponse = new NotificationResponseDTO();
		notificationResponse.setMessage("Notification sent to registered contact details");
		notificationResponse.setStatus("success");
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(notificationResponse);
		ResidentReprintResponseDto residentResponse = residentServiceImpl.reqPrintUin(residentReqDto);
		assertEquals("10008200070004620191203115734", residentResponse.getRegistrationId());

	}

	@Test(expected = ResidentServiceException.class)
	public void validateOtpException() throws OtpValidationFailedException, IOException, ResidentServiceCheckedException {
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(false);
		residentServiceImpl.reqPrintUin(residentReqDto);

	}

	@Test(expected = ResidentServiceException.class)
	public void reprintApiException() throws OtpValidationFailedException, IOException, ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(null);
		residentServiceImpl.reqPrintUin(residentReqDto);

	}

	@Test(expected = ResidentServiceException.class)
	public void testOtpValidationException() throws OtpValidationFailedException, ResidentServiceCheckedException {
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenThrow(OtpValidationFailedException.class);
		residentServiceImpl.reqPrintUin(residentReqDto);
	}

	@Test(expected = ResidentServiceException.class)
	public void testApiResourceAccessException()
			throws ApisResourceAccessException, IOException, OtpValidationFailedException, ResidentServiceCheckedException {
		HttpClientErrorException exp = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(new ApisResourceAccessException("badgateway", exp));
		residentServiceImpl.reqPrintUin(residentReqDto);
	}

	@Test(expected = ResidentServiceException.class)
	public void testApiResourceAccessExceptionServer()
			throws ApisResourceAccessException, IOException, OtpValidationFailedException, ResidentServiceCheckedException {
		HttpServerErrorException exp = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(new ApisResourceAccessException("badgateway", exp));
		residentServiceImpl.reqPrintUin(residentReqDto);
	}

	@Test(expected = ResidentServiceException.class)
	public void testApiResourceAccessExceptionUnknown()
			throws ApisResourceAccessException, IOException, OtpValidationFailedException, ResidentServiceCheckedException {
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(new ApisResourceAccessException("badgateway", new RuntimeException()));
		residentServiceImpl.reqPrintUin(residentReqDto);
	}

	@Test(expected = ResidentServiceException.class)
	public void testTokenGeneratorIOException()
			throws ApisResourceAccessException, IOException, OtpValidationFailedException, ResidentServiceCheckedException {
		Mockito.when(tokenGenerator.getRegprocToken()).thenThrow(new IOException());
		Mockito.when(env.getProperty(ApiName.REPRINTUIN.name()))
				.thenReturn("https://int.mosip.io/registrationprocessor/v1/requesthandler/reprint");
		residentServiceImpl.reqPrintUin(residentReqDto);
	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void notificationServiceException() throws ApisResourceAccessException, OtpValidationFailedException,
			IOException, ResidentServiceCheckedException {
		ResponseWrapper<RegProcRePrintResponseDto> response = new ResponseWrapper<>();
		RegProcRePrintResponseDto reprintResp = new RegProcRePrintResponseDto();
		reprintResp.setMessage("sent to packet receiver");
		reprintResp.setRegistrationId("10008200070004620191203115734");
		reprintResp.setStatus("success");
		response.setResponse(reprintResp);
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(response);
		NotificationResponseDTO notificationResponse = new NotificationResponseDTO();
		notificationResponse.setMessage("Notification sent to registered contact details");
		notificationResponse.setStatus("success");
		Mockito.when(notificationService.sendNotification(Mockito.any()))
				.thenThrow(new ResidentServiceCheckedException());
		residentServiceImpl.reqPrintUin(residentReqDto);

	}

}
