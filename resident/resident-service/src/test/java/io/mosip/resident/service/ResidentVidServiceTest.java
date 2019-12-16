package io.mosip.resident.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.*;
import io.mosip.resident.exception.*;
import io.mosip.resident.service.impl.ResidentVidServiceImpl;
import io.mosip.resident.util.JsonUtil;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import io.mosip.resident.util.Utilitiy;

import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@RefreshScope
@ContextConfiguration
public class ResidentVidServiceTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private Environment env;

    @Mock
    private ResidentServiceRestClient residentServiceRestClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private IdAuthService idAuthService;

    @Mock
    private ObjectMapper mapper;
    
    @Mock
	private Utilitiy utilitiy;

    private VidRequestDto requestDto;
    
    private VidRevokeRequestDTO vidRevokeRequest;

    @InjectMocks
    private ResidentVidService residentVidService = new ResidentVidServiceImpl();
    private JSONObject identity;
    
    @Before
    public void setup() throws IOException, ResidentServiceCheckedException {

        requestDto = new VidRequestDto();
        requestDto.setOtp("123");
        requestDto.setTransactionID("12345");
        requestDto.setIndividualIdType(IdType.UIN.name());
        requestDto.setIndividualId("1234567890");
        requestDto.setVidType("Temporary");

        NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
        notificationResponseDTO.setMessage("Vid successfully generated");

        when(tokenGenerator.getRegprocToken()).thenReturn("token");
        when(notificationService.sendNotification(any(NotificationRequestDto.class))).thenReturn(notificationResponseDTO);
   
        ClassLoader classLoader = getClass().getClassLoader();
		File idJson = new File(classLoader.getResource("ID.json").getFile());
		InputStream is = new FileInputStream(idJson);
		String idJsonString = IOUtils.toString(is, "UTF-8");
		identity = JsonUtil.readValue(idJsonString, JSONObject.class);
		
		vidRevokeRequest = new VidRevokeRequestDTO();

		vidRevokeRequest.setIndividualId("2038096257310540");
		vidRevokeRequest.setIndividualIdType(IdType.VID.name());
		vidRevokeRequest.setOtp("974436");
		vidRevokeRequest.setTransactionID("1111122222");
		vidRevokeRequest.setVidStatus("REVOKE");
		
		NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
		notificationRequestDto.setId("1234567");
		notificationRequestDto.setIdType(IdType.UIN);
    }

    @Test
    public void generateVidSuccessTest() throws OtpValidationFailedException, IOException, ApisResourceAccessException, ResidentServiceCheckedException {

        String vid = "12345";
        VidGeneratorResponseDto vidGeneratorResponseDto = new VidGeneratorResponseDto();
        vidGeneratorResponseDto.setVidStatus("Active");
        vidGeneratorResponseDto.setVID(vid);
        ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
        response.setResponsetime(DateUtils.getCurrentDateTimeString());
        response.setResponse(vidGeneratorResponseDto);

        doReturn(objectMapper.writeValueAsString(vidGeneratorResponseDto)).when(mapper).writeValueAsString(any());
        doReturn(vidGeneratorResponseDto).when(mapper).readValue(anyString(), any(Class.class));
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(residentServiceRestClient.postApi(any(), any(), any(), any(),
                any())).thenReturn(response);

        ResponseWrapper<VidResponseDto> result = residentVidService.generateVid(requestDto);

        assertTrue("Expected Vid should be 12345", result.getResponse().getVid().equalsIgnoreCase(vid));
    }

    @Test(expected = OtpValidationFailedException.class)
    public void otpValidationFailedTest() throws ResidentServiceCheckedException, OtpValidationFailedException {
    	
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.FALSE);
        residentVidService.generateVid(requestDto);
    }

    @Test(expected = VidAlreadyPresentException.class)
    public void vidAlreadyExistsExceptionTest() throws ResidentServiceCheckedException, OtpValidationFailedException, ApisResourceAccessException {

        String VID_ALREADY_EXISTS_ERROR_CODE = "IDR-VID-003";

        ServiceError serviceError = new ServiceError();
        serviceError.setErrorCode(VID_ALREADY_EXISTS_ERROR_CODE);
        serviceError.setMessage("Vid already present");
        ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
        response.setResponsetime(DateUtils.getCurrentDateTimeString());
        response.setErrors(Lists.newArrayList(serviceError));

        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);

        when(residentServiceRestClient.postApi(any(), any(), any(), any(),
                any())).thenReturn(response);

        residentVidService.generateVid(requestDto);
    }

    @Test(expected = VidCreationException.class)
    public void vidCreationExceptionTest() throws ResidentServiceCheckedException, OtpValidationFailedException, ApisResourceAccessException {

        String ERROR_CODE = "err";

        ServiceError serviceError = new ServiceError();
        serviceError.setErrorCode(ERROR_CODE);
        serviceError.setMessage("Vid already present");
        ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
        response.setResponsetime(DateUtils.getCurrentDateTimeString());
        response.setErrors(Lists.newArrayList(serviceError));

        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);

        when(residentServiceRestClient.postApi(any(), any(), any(), any(),
                any())).thenReturn(response);

        residentVidService.generateVid(requestDto);
    }

    @Test(expected = VidCreationException.class)
    public void apiResourceAccessExceptionTest() throws ResidentServiceCheckedException, OtpValidationFailedException, ApisResourceAccessException {

        String ERROR_CODE = "err";

        ServiceError serviceError = new ServiceError();
        serviceError.setErrorCode(ERROR_CODE);
        serviceError.setMessage("Vid already present");
        ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
        response.setResponsetime(DateUtils.getCurrentDateTimeString());
        response.setErrors(Lists.newArrayList(serviceError));

        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);

        when(residentServiceRestClient.postApi(any(), any(), any(), any(),
                any())).thenThrow(new ApisResourceAccessException());

        residentVidService.generateVid(requestDto);
    }
    
    @Test
	public void revokeVidSuccessTest() throws OtpValidationFailedException, IOException, ApisResourceAccessException,
			ResidentServiceCheckedException {

		String vid = "2038096257310540";
	
		VidGeneratorResponseDto dto = new VidGeneratorResponseDto();
		dto.setVidStatus("Deactive");

		ResponseWrapper<VidGeneratorResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(dto);
		responseWrapper.setVersion("v1");
		responseWrapper.setResponsetime(DateUtils.getCurrentDateTimeString());
		
		when(utilitiy.retrieveIdrepoJson(anyString(), any())).thenReturn(JsonUtil.getJSONObject(identity, "identity"));
	
		doReturn(objectMapper.writeValueAsString(dto)).when(mapper).writeValueAsString(any());
		doReturn(dto).when(mapper).readValue(anyString(), any(Class.class));
		when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
		when(residentServiceRestClient.postApi(any(), any(), any(), any(), any())).thenReturn(responseWrapper);

		ResponseWrapper<VidRevokeResponseDTO> result2 = residentVidService.revokeVid(vidRevokeRequest,vid);

		assertEquals("Vid successfully generated", result2.getResponse().getMessage().toString());
	}
    
    @Test(expected = OtpValidationFailedException.class)
    public void otpValidationFailedTest1() throws ResidentServiceCheckedException, OtpValidationFailedException {
    	String vid = "2038096257310540";
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.FALSE);

        residentVidService.revokeVid(vidRevokeRequest, vid);
    }
    
    @Test(expected = VidRevocationException.class)
    public void apiResourceAccessExceptionTest2() throws ResidentServiceCheckedException, OtpValidationFailedException, ApisResourceAccessException {

        String ERROR_CODE = "err";
        String vid = "2038096257310540";
        ServiceError serviceError = new ServiceError();
        serviceError.setErrorCode(ERROR_CODE);
        serviceError.setMessage("No Record Found");
        ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
        response.setResponsetime(DateUtils.getCurrentDateTimeString());
        response.setErrors(Lists.newArrayList(serviceError));
        when(utilitiy.retrieveIdrepoJson(anyString(), any())).thenReturn(JsonUtil.getJSONObject(identity, "identity"));
    	
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);

        when(residentServiceRestClient.postApi(any(), any(), any(), any(),
                any())).thenThrow(new ApisResourceAccessException());

        residentVidService.revokeVid(vidRevokeRequest,vid);
    }
    
    @Test(expected = VidRevocationException.class)
    public void idRepoAppExceptionTest() throws ResidentServiceCheckedException, OtpValidationFailedException, ApisResourceAccessException {

        String ERROR_CODE = "err";
        String vid = "2038096257310540";
        ServiceError serviceError = new ServiceError();
        serviceError.setErrorCode(ERROR_CODE);
        serviceError.setMessage("No Record Found");
        ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
        response.setResponsetime(DateUtils.getCurrentDateTimeString());
        response.setErrors(Lists.newArrayList(serviceError));
        when(utilitiy.retrieveIdrepoJson(anyString(), any())).thenThrow(VidRevocationException.class);
    	
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(idAuthService.validateOtp(anyString(), anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);

        residentVidService.revokeVid(vidRevokeRequest,vid);
    }
}
