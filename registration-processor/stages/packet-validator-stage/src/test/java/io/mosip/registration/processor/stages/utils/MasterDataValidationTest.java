package io.mosip.registration.processor.stages.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.IdentityJsonValues;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.StatusResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
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
	private String identityMappingjsonString;

	StatusResponseDto statusResponseDto;

	String jsonString = null;
	private static final String CONFIG_SERVER_URL = "url";

	MasterDataValidation masterDataValidation;

	@Before
	public void setUp() throws Exception {
		InputStream inputStream = new FileInputStream("src/test/resources/ID.json");
		byte[] bytes = IOUtils.toByteArray(inputStream);
		jsonString = new String(bytes);

		ClassLoader classLoader = getClass().getClassLoader();
		File identityMappingjson = new File(classLoader.getResource("RegistrationProcessorIdentity.json").getFile());
		InputStream identityMappingjsonStream = new FileInputStream(identityMappingjson);
		try {
			identityMappingjsonString = IOUtils.toString(identityMappingjsonStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		IdentityJsonValues gender = new IdentityJsonValues();
		IdentityJsonValues region = new IdentityJsonValues();
		IdentityJsonValues province = new IdentityJsonValues();
		IdentityJsonValues city = new IdentityJsonValues();
		IdentityJsonValues postalcode = new IdentityJsonValues();

		gender.setValue("female");
		region.setValue("Rabat-Salé-Kénitra");
		province.setValue("Rabat");
		city.setValue("bng-south");
		postalcode.setValue("10000");

		registrationProcessorIdentity = new RegistrationProcessorIdentity();

		identityDemo.setGender(gender);
		identityDemo.setRegion(region);
		identityDemo.setProvince(province);
		identityDemo.setCity(city);
		identityDemo.setPostalCode(postalcode);
		registrationProcessorIdentity.setIdentity(identityDemo);

		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("valid");

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", CONFIG_SERVER_URL, "RegistrationProcessorIdentity.json")
				.thenReturn(identityMappingjsonString);
		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn(CONFIG_SERVER_URL);
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("RegistrationProcessorIdentity.json");

		Mockito.when(mapIdentityJsonStringToObject.readValue(anyString(), Mockito.any(Class.class)))
				.thenReturn(registrationProcessorIdentity);

		when(env.getProperty("registration.processor.idjson.attributes"))
				.thenReturn("gender,region,province,city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);
		masterDataValidation = new MasterDataValidation(registrationStatusDto, env, registrationProcessorRestService,
				utility, regProcessorIdentityJson);
	}

	@Test
	public void testMasterDataValidationSuccess() throws Exception {

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertTrue("Test for successful Structural Validation", isMasterDataValidated);

	}

	@Test
	public void testMasterDataValidationGenderFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");

		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Gender name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationRegionFailure() throws Exception {

		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");
		when(env.getProperty("registration.processor.idjson.attributes")).thenReturn("region,province,city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Region name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationProvinceFailure() throws Exception {

		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");
		when(env.getProperty("registration.processor.idjson.attributes")).thenReturn("province,city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Province name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationCityFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");
		when(env.getProperty("registration.processor.idjson.attributes")).thenReturn("city,postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for City name failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationPostalCodeFailure() throws Exception {
		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("invalid");
		when(env.getProperty("registration.processor.idjson.attributes")).thenReturn("postalcode");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);

		boolean isMasterDataValidated = masterDataValidation.validateMasterData(jsonString);
		assertFalse("Test for Postal code failure", isMasterDataValidated);
	}

	@Test
	public void testMasterDataValidationGenderApiException() throws Exception {

		when(env.getProperty("registration.processor.idjson.attributes"))
				.thenReturn("gender,region,province,city,postalcode");
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

		when(env.getProperty("registration.processor.idjson.attributes")).thenReturn("region,province,city,postalcode");
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
