package io.mosip.registration.processor.status.decryptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.CryptomanagerResponseDto;
import io.mosip.registration.processor.status.exception.PacketDecryptionFailureException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DecryptorTest.class })
public class DecryptorTest {

	@InjectMocks
	private Decryptor decryptor;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;
	private CryptomanagerResponseDto cryptomanagerResponseDto;
	private String data;
	private File encrypted;
	private InputStream inputStream;
	@Mock
	private Environment env;

	@Before
	public void setup() throws FileNotFoundException {
		data = "bW9zaXA";
		cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setData(data);

		/*
		 * ClassLoader classLoader = getClass().getClassLoader(); encrypted = new
		 * File(classLoader.getResource("84071493960000320190110145452.zip").getFile());
		 * inputStream = new FileInputStream(encrypted);
		 */
		when(env.getProperty("mosip.registration.processor.crypto.decrypt.id"))
				.thenReturn("mosip.cryptomanager.decrypt");
		when(env.getProperty("mosip.registration.processor.application.version")).thenReturn("1.0");
		when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

	@Test
	public void decryptTest() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {

		CryptomanagerResponseDto cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setData("bW9zaXA");
		ResponseWrapper<CryptomanagerResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerResponseDto);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any())).thenReturn(response);
		String decryptedString = decryptor.decrypt(data, "10011", "2019-05-07T05:13:55.704Z");

		assertEquals("mosip", decryptedString);

	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void HttpClientErrorExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {
		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST,
				"Invalid request");
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpClientErrorException);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		String decryptedString = decryptor.decrypt(data, "10011", "2019-05-07T05:13:55.704Z");
	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void HttpServerErrorExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {

		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpServerErrorException httpServerErrorException = new HttpServerErrorException(
				HttpStatus.INTERNAL_SERVER_ERROR, "KER-FSE-004:encrypted data is corrupted or not base64 encoded");
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpServerErrorException);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);

		String decryptedString = decryptor.decrypt(data, "10011", "2019-05-07T05:13:55.704Z");

	}

	@Test(expected = ApisResourceAccessException.class)
	public void PacketDecryptionFailureExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {

		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException(
				"Packet Decryption failure");
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		String decryptedString = decryptor.decrypt(data, "10011", "2019-05-07T05:13:55.704Z");
	}

}
