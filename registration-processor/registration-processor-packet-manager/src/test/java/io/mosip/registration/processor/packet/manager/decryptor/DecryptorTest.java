package io.mosip.registration.processor.packet.manager.decryptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.spi.decryptor.Decryptor;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.PacketManagerBootApplication;
import io.mosip.registration.processor.packet.manager.dto.CryptomanagerResponseDto;
import io.mosip.registration.processor.packet.manager.dto.DecryptResponseDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketManagerBootApplication.class)
public class DecryptorTest {

	@Autowired
	private Decryptor decryptor;

	@MockBean
	private RegistrationProcessorRestClientService<Object> restClientService;
	@MockBean
	private AuditLogRequestBuilder auditLogRequestBuilder;
	private CryptomanagerResponseDto cryptomanagerResponseDto;
	private String data;
	private File encrypted;
	private InputStream inputStream;

	@Before
	public void setup() throws FileNotFoundException {
		data = "bW9zaXA";
		cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setResponse(new DecryptResponseDto(data));

		ClassLoader classLoader = getClass().getClassLoader();
		encrypted = new File(classLoader.getResource("84071493960000320190110145452.zip").getFile());
		inputStream = new FileInputStream(encrypted);
	}

	@Test
	public void decryptTest() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {

		CryptomanagerResponseDto cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setResponse(new DecryptResponseDto(data));
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any())).thenReturn(cryptomanagerResponseDto);
		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");
		String decryptedString = IOUtils.toString(decryptedStream, "UTF-8");
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
		decryptor.decrypt(inputStream, "84071493960000320190110145452");
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

		decryptor.decrypt(inputStream, "84071493960000320190110145452");

	}

	@Test(expected = ApisResourceAccessException.class)
	public void PacketDecryptionFailureExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {

		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException(
				"Packet Decryption failure");
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		decryptor.decrypt(inputStream, "84071493960000320190110145452");

	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void invalidPacketFormatTest() throws PacketDecryptionFailureException, ApisResourceAccessException {
		decryptor.decrypt(inputStream, "01901101456");

	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void invalidPacketFormatParsingDateTimeTest()
			throws PacketDecryptionFailureException, ApisResourceAccessException {
		decryptor.decrypt(inputStream, "8407149396000032019T110145452");

	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void decryptErrorCryptoManagerTest()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		CryptomanagerResponseDto cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setErrors(Arrays.asList(new ServiceError("Error-001", "Error-Message-001")));
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any())).thenReturn(cryptomanagerResponseDto);
		decryptor.decrypt(inputStream, "84071493960000320190110145452");
	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void decryptIOExceptionTest()
			throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {
		InputStream is = Mockito.mock(InputStream.class);
		doThrow(IOException.class).when(is).close();
		InputStream decryptedStream = decryptor.decrypt(is, "84071493960000320190110145452");
		String decryptedString = IOUtils.toString(decryptedStream, "UTF-8");
		assertEquals("mosip", decryptedString);

	}
}
