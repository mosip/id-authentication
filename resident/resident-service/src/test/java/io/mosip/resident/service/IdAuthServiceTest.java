package io.mosip.resident.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.AuthTypeStatus;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.AuthResponseDTO;
import io.mosip.resident.dto.AuthTypeStatusResponseDto;
import io.mosip.resident.dto.ErrorDTO;
import io.mosip.resident.dto.IdAuthResponseDto;
import io.mosip.resident.dto.PublicKeyResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.impl.IdAuthServiceImpl;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

@RunWith(MockitoJUnitRunner.class)
@RefreshScope
@ContextConfiguration
public class IdAuthServiceTest {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private ObjectMapper mapper;

	@Mock
	private SecretKey secretKey;

	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private TokenGenerator tokenGenerator;

	@Mock
	private Environment environment;

	@Mock
	private ResidentServiceRestClient restClient;

	@Mock
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

	@InjectMocks
	private IdAuthService idAuthService = new IdAuthServiceImpl();

	@Before
	public void setup() {
		when(environment.getProperty(ApiName.KERNELENCRYPTIONSERVICE.name())).thenReturn("http://localhost:8080");

	}

	@Test
	public void testAuthTypeStatusUpdateSuccess() throws ApisResourceAccessException, ResidentServiceCheckedException {
		AuthTypeStatusResponseDto authTypeStatusResponseDto = new AuthTypeStatusResponseDto();
		when(restClient.postApi(any(), any(), any(), any(), any())).thenReturn(authTypeStatusResponseDto);
		List<String> authTypes = new ArrayList<>();
		authTypes.add("bio-FIR");
		boolean isUpdated = idAuthService.authTypeStatusUpdate("1234567891", "UIN", authTypes, AuthTypeStatus.LOCK);
		assertTrue(isUpdated);
	}

	@Test(expected = ApisResourceAccessException.class)
	public void testAuthTypeStatusUpdateFailure() throws ApisResourceAccessException, ResidentServiceCheckedException {

		when(restClient.postApi(any(), any(), any(), any(), any())).thenThrow(new ApisResourceAccessException());
		List<String> authTypes = new ArrayList<>();
		authTypes.add("bio-FIR");
		boolean isUpdated = idAuthService.authTypeStatusUpdate("1234567891", "UIN", authTypes, AuthTypeStatus.LOCK);
		assertTrue(isUpdated);
	}

	@Test
	public void validateOtpSuccessTest() throws IOException, ApisResourceAccessException, OtpValidationFailedException {
		String transactionID = "12345";
		String individualId = "individual";
		String individualIdType = IdType.UIN.name();
		String otp = "12345";

		String request = "request";

		IdAuthResponseDto authResponse = new IdAuthResponseDto();
		authResponse.setAuthStatus(true);
		AuthResponseDTO response = new AuthResponseDTO();
		response.setResponse(authResponse);

		PublicKeyResponseDto responseDto = new PublicKeyResponseDto();
		responseDto.setPublicKey(
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApGh1E3bppaeL8pznuRFx-diebah_ZIcIqs_uCJFvK-x2FkWi0F73tzTYYXE6R-peMmfgjMz8OVIcILEFylVpeQEPHy9ChNEhdSI861zSDbhW_aPPUMWgUOsMzD3b_b5IPLKODUWsGoeY2U8uwjLeVQjje89RK5z080C8SmhX0NRNPkfgX4K71kpqcP6ROKQMhHZ5m8ezdVb_AogndFx8Jw8A1CgIOPfFMY7z-l5UbH8afOydrtH2nShb5HAal5vX4tGOyv0KsZIrBR3YquNfw9vEzmHfrvt_0xrYubasbh3_Fnal57LY-GdQ7XKf9OPXJGDL4B85Z_gkbvefYhFflwIDAQAB");
		ResponseWrapper<PublicKeyResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(responseDto);

		when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		when(encryptor.symmetricEncrypt(any(), any(), any())).thenReturn(request.getBytes());
		when(tokenGenerator.getToken()).thenReturn("token");
		when(restClient.getApi(any(), any(Class.class), any())).thenReturn(responseWrapper);

		doReturn(objectMapper.writeValueAsString(responseDto)).when(mapper).writeValueAsString(any());
		doReturn(responseDto).when(mapper).readValue(anyString(), any(Class.class));

		when(encryptor.asymmetricEncrypt(any(), any())).thenReturn(request.getBytes());

		when(tokenGenerator.getToken()).thenReturn("token");

		when(restClient.postApi(any(), any(), any(), any(Class.class), any())).thenReturn(response);

		boolean result = idAuthService.validateOtp(transactionID, individualId, individualIdType, otp);

		assertThat("Expected otp validation successful", result, is(true));
	}

	@Test(expected = OtpValidationFailedException.class)
	public void otpValidationFailedTest()
			throws IOException, ApisResourceAccessException, OtpValidationFailedException {
		String transactionID = "12345";
		String individualId = "individual";
		String individualIdType = IdType.UIN.name();
		String otp = "12345";

		String request = "request";

		PublicKeyResponseDto responseDto = new PublicKeyResponseDto();
		responseDto.setPublicKey(
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApGh1E3bppaeL8pznuRFx-diebah_ZIcIqs_uCJFvK-x2FkWi0F73tzTYYXE6R-peMmfgjMz8OVIcILEFylVpeQEPHy9ChNEhdSI861zSDbhW_aPPUMWgUOsMzD3b_b5IPLKODUWsGoeY2U8uwjLeVQjje89RK5z080C8SmhX0NRNPkfgX4K71kpqcP6ROKQMhHZ5m8ezdVb_AogndFx8Jw8A1CgIOPfFMY7z-l5UbH8afOydrtH2nShb5HAal5vX4tGOyv0KsZIrBR3YquNfw9vEzmHfrvt_0xrYubasbh3_Fnal57LY-GdQ7XKf9OPXJGDL4B85Z_gkbvefYhFflwIDAQAB");
		ResponseWrapper<PublicKeyResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(responseDto);

		when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		when(encryptor.symmetricEncrypt(any(), any(), any())).thenReturn(request.getBytes());
		when(tokenGenerator.getToken()).thenReturn("token");
		when(restClient.getApi(any(), any(Class.class), any())).thenReturn(responseWrapper);

		doReturn(objectMapper.writeValueAsString(responseDto)).when(mapper).writeValueAsString(any());
		doReturn(responseDto).when(mapper).readValue(anyString(), any(Class.class));

		when(encryptor.asymmetricEncrypt(any(), any())).thenReturn(request.getBytes());

		when(tokenGenerator.getToken()).thenReturn("token");

		when(restClient.postApi(any(), any(), any(), any(Class.class), any()))
				.thenThrow(new ApisResourceAccessException());

		idAuthService.validateOtp(transactionID, individualId, individualIdType, otp);
	}

	@Test(expected = OtpValidationFailedException.class)
	public void idAuthErrorsTest() throws IOException, ApisResourceAccessException, OtpValidationFailedException {
		String transactionID = "12345";
		String individualId = "individual";
		String individualIdType = IdType.UIN.name();
		String otp = "12345";

		String request = "request";

		ErrorDTO errorDTO = new ErrorDTO("errorId", "errorMessage");
		AuthResponseDTO response = new AuthResponseDTO();
		response.setErrors(Lists.newArrayList(errorDTO));

		PublicKeyResponseDto responseDto = new PublicKeyResponseDto();
		responseDto.setPublicKey(
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApGh1E3bppaeL8pznuRFx-diebah_ZIcIqs_uCJFvK-x2FkWi0F73tzTYYXE6R-peMmfgjMz8OVIcILEFylVpeQEPHy9ChNEhdSI861zSDbhW_aPPUMWgUOsMzD3b_b5IPLKODUWsGoeY2U8uwjLeVQjje89RK5z080C8SmhX0NRNPkfgX4K71kpqcP6ROKQMhHZ5m8ezdVb_AogndFx8Jw8A1CgIOPfFMY7z-l5UbH8afOydrtH2nShb5HAal5vX4tGOyv0KsZIrBR3YquNfw9vEzmHfrvt_0xrYubasbh3_Fnal57LY-GdQ7XKf9OPXJGDL4B85Z_gkbvefYhFflwIDAQAB");
		ResponseWrapper<PublicKeyResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(responseDto);

		when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		when(encryptor.symmetricEncrypt(any(), any(), any())).thenReturn(request.getBytes());
		when(tokenGenerator.getToken()).thenReturn("token");
		when(restClient.getApi(any(), any(Class.class), any())).thenReturn(responseWrapper);

		doReturn(objectMapper.writeValueAsString(responseDto)).when(mapper).writeValueAsString(any());
		doReturn(responseDto).when(mapper).readValue(anyString(), any(Class.class));

		when(encryptor.asymmetricEncrypt(any(), any())).thenReturn(request.getBytes());

		when(tokenGenerator.getToken()).thenReturn("token");

		when(restClient.postApi(any(), any(), any(), any(Class.class), any())).thenReturn(response);

		idAuthService.validateOtp(transactionID, individualId, individualIdType, otp);
	}
}
