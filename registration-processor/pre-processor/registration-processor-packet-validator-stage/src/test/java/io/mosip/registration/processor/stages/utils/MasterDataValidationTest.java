package io.mosip.registration.processor.stages.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.StatusResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, JsonUtil.class })
public class MasterDataValidationTest {

	@Mock
	RegistrationProcessorIdentity regProcessorIdentityJson;

	@Mock
	private Environment env;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Mock
	Utilities utility;

	@Mock
	InternalRegistrationStatusDto registrationStatusDto;

	@Mock
	RegistrationProcessorIdentity registrationProcessorIdentity;

	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	Identity identityDemo = new Identity();

	StatusResponseDto statusResponseDto;

	String jsonString = null;

	MasterDataValidation masterDataValidation;

	private static final String PRIMARY_LANGUAGE = "mosip.primary-language";

	private static final String SECONDARY_LANGUAGE = "mosip.secondary-language";

	private static final String ATTRIBUTES = "registration.processor.masterdata.validation.attributes";

	@Before
	public void setUp() throws Exception {
		InputStream inputStream = new FileInputStream("src/test/resources/ID.json");
		byte[] bytes = IOUtils.toByteArray(inputStream);
		jsonString = new String(bytes);

		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("valid");
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(statusResponseDto);
		PowerMockito.mockStatic(Utilities.class);

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		Mockito.when(mapIdentityJsonStringToObject.readValue(anyString(), Mockito.any(Class.class)))
				.thenReturn(registrationProcessorIdentity);

		when(env.getProperty(anyString())).thenReturn("gender");
		when(env.getProperty(PRIMARY_LANGUAGE)).thenReturn("eng");
		when(env.getProperty(SECONDARY_LANGUAGE)).thenReturn("ara");
		when(env.getProperty(ATTRIBUTES)).thenReturn("gender,region,province,city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);
		masterDataValidation = new MasterDataValidation(env, registrationProcessorRestService, utility);
	}

	@Test
	public void testMasterDataValidationSuccess() throws Exception {

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertTrue("Test for successful Master Data Validation", isMasterDataValidated);

	}

	@Test
	public void testMasterDataValidationResouceFailure() throws Exception {

		when(env.getProperty(anyString())).thenReturn(null);
		when(env.getProperty(ATTRIBUTES)).thenReturn("gender");
		when(env.getProperty(PRIMARY_LANGUAGE)).thenReturn("eng");
		when(env.getProperty(SECONDARY_LANGUAGE)).thenReturn("ara");
		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for resource not found", isMasterDataValidated);

	}

	@Test(expected = IdentityNotFoundException.class)
	public void testIOException() throws Exception {
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "getJSONObject", any(), any()).thenReturn(null);
		masterDataValidation.validateMasterData(jsonString);

	}

	@Test
	public void testMasterDataValidationGenderFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");

		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(statusResponseDto);

		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Gender name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationRegionFailure() throws Exception {

		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");

		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(statusResponseDto);

		when(env.getProperty(ATTRIBUTES)).thenReturn("region,province,city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Region name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationProvinceFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(statusResponseDto);


		when(env.getProperty(ATTRIBUTES)).thenReturn("province,city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Province name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationCityFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");

		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(statusResponseDto);

		when(env.getProperty(ATTRIBUTES)).thenReturn("city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for City name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationPostalCodeFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");

		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(statusResponseDto);

		when(env.getProperty(ATTRIBUTES)).thenReturn("postalCode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Postal code failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationGenderApiException() throws Exception {

		when(env.getProperty(ATTRIBUTES)).thenReturn("gender,region,province,city,postalcode");
		byte[] responseBody = "{\"timestamp\":1548931133376,\"status\":400,\"errors\":[{\"errorCode\":\"KER\",\"errorMessage\":\"Invalid \"}]}"
				.getBytes();
		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST,
				"Invalid request", null, responseBody, null);
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpClientErrorException);

		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Api resource Access Exception in Gender Name Api", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationLocationApiException() throws Exception {

		when(env.getProperty(ATTRIBUTES)).thenReturn("region,province,city,postalcode");
		byte[] responseBody = "{\"timestamp\":1548931133376,\"status\":400,\"errors\":[{\"errorCode\":\"KER\",\"errorMessage\":\"Invalid \"}]}"
				.getBytes();
		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST,
				"Invalid request", null, responseBody, null);
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpClientErrorException);

		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Api resource Access Exception in Location Name Api", isMasterDataValidated);
	}

}
