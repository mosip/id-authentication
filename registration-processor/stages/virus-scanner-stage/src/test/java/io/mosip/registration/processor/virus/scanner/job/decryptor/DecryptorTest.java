package io.mosip.registration.processor.virus.scanner.job.decryptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.dto.CryptomanagerResponseDto;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DecryptorTest.class })
public class DecryptorTest {

	@InjectMocks
	private Decryptor decryptor;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	private CryptomanagerResponseDto cryptomanagerResponseDto;
	private String data;
	private File encrypted;
	private InputStream inputStream;

	@Before
	public void setup() throws FileNotFoundException {
		data = "bW9zaXA";
		cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setData(data);

		ClassLoader classLoader = getClass().getClassLoader();
		encrypted = new File(classLoader.getResource("84071493960000320190110145452.zip").getFile());
		inputStream = new FileInputStream(encrypted);

	}

	@Test
	public void decryptTest() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {

		CryptomanagerResponseDto cryptomanagerResponseDto = new CryptomanagerResponseDto();
		cryptomanagerResponseDto.setData("bW9zaXA");
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
		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");
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

		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");

	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void PacketDecryptionFailureExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {

		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException(
				"Packet Decryption failure");
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");

	}
	
	@Test(expected = PacketDecryptionFailureException.class)
	public void invalidPacketFormatTest() throws PacketDecryptionFailureException {
		InputStream decryptedStream = decryptor.decrypt(inputStream, "019011014");

	}
	@Test(expected = PacketDecryptionFailureException.class)
	public void invalidPacketFormatParsingDateTimeTest() throws PacketDecryptionFailureException {
		InputStream decryptedStream = decryptor.decrypt(inputStream, "8407149396000032019T110145452");

	}
}
