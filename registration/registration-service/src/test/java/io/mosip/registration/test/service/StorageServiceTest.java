package io.mosip.registration.test.service;

import java.io.File;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.StorageServiceImpl;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileUtils.class })
public class StorageServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private StorageServiceImpl storageService;
	@Mock
	private Environment environment;

	@Before
	public void initialize() {
		when(environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION)).thenReturn("PacketStore");
		when(environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT)).thenReturn("dd-MMM-yyyy");

		PowerMockito.spy(FileUtils.class);
	}

	@Test
	public void testLocalStorage() throws Exception {
		PowerMockito.doNothing().when(FileUtils.class, "copyToFile", Mockito.any(InputStream.class),
				Mockito.any(File.class));
		Assert.assertNotNull(storageService.storeToDisk("1234567890123", "demo".getBytes()));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testRuntimeException() throws RegBaseCheckedException {
		storageService.storeToDisk(null, "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testIOException() throws Exception {
		PowerMockito.doThrow(new IOException("PCM", "File Not Found")).when(FileUtils.class, "copyToFile",
				Mockito.any(InputStream.class), Mockito.any(File.class));
		storageService.storeToDisk("12343455657676787", "packet.zip".getBytes());
	}
}
