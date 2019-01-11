package io.mosip.registration.processor.virus.scanner.job.decryptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Decryptor.class })
public class DecryptorTest {

	@InjectMocks
	private Decryptor decryptor;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Test
	public void decryptTest() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File encrypted = new File(classLoader.getResource("84071493960000320190110145452.zip").getFile());
		InputStream inputStream = new FileInputStream(encrypted);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenReturn("{\r\n" + "  \"data\": \"bW9zaXA\"\r\n" + "}");
		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");
		String decryptedString = IOUtils.toString(decryptedStream, "UTF-8");
		assertEquals("mosip", decryptedString);

	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void PacketDecryptionFailureExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {
		ClassLoader classLoader = getClass().getClassLoader();
		File encrypted = new File(classLoader.getResource("84071493960000320190110145452.zip").getFile());
		InputStream inputStream = new FileInputStream(encrypted);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any())).thenReturn(
				"{\"timestamp\":1547097805735,\"status\":400,\"errors\":[{\"errorCode\":\"KER-FSE-004\",\"errorMessage\":\"encrypted data is corrupted or not base64 encoded\"}]}");
		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");
	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void ApisResourceAccessExceptionTest()
			throws FileNotFoundException, ApisResourceAccessException, PacketDecryptionFailureException {
		ClassLoader classLoader = getClass().getClassLoader();
		File encrypted = new File(classLoader.getResource("84071493960000320190110145452.zip").getFile());
		InputStream inputStream = new FileInputStream(encrypted);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(new ApisResourceAccessException(
						"Error from registartion-client-service while hitting the kernel cryptomanager"));
		InputStream decryptedStream = decryptor.decrypt(inputStream, "84071493960000320190110145452");

	}

}
