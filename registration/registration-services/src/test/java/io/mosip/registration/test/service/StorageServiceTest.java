package io.mosip.registration.test.service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.StorageServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileUtils.class, ApplicationContext.class })
public class StorageServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private StorageServiceImpl storageService;

	@Before
	public void initialize() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.PKT_STORE_LOC, "..//PacketStore");
		appMap.put(RegistrationConstants.PACKET_STORE_DATE_FORMAT, "dd-MMM-yyyy");

		storageService.setPacketStoreLocation("..//PacketStore");
		PowerMockito.mockStatic(ApplicationContext.class, FileUtils.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
	}

	@Test
	public void testLocalStorage() throws Exception {
		PowerMockito.doNothing().when(FileUtils.class, "copyToFile", Mockito.any(InputStream.class),
				Mockito.any(File.class));

		
		Assert.assertNotNull(storageService.storeToDisk("1234567890123", "demo".getBytes()));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testRuntimeException() throws Exception {
		PowerMockito.doThrow(new RuntimeException("Unable to save")).when(FileUtils.class, "copyToFile",
				Mockito.any(InputStream.class), Mockito.any(File.class));

		storageService.storeToDisk("1213242422", "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testIOException() throws Exception {
		PowerMockito.doThrow(new IOException("PCM", "File Not Found")).when(FileUtils.class, "copyToFile",
				Mockito.any(InputStream.class), Mockito.any(File.class));

		storageService.storeToDisk("12343455657676787", "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidRID() throws Exception {
		storageService.storeToDisk("", "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidDataToStore() throws Exception {
		storageService.storeToDisk("11313131", "".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testPacketStoreParamNotFound() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(new HashMap<>()).when(ApplicationContext.class, "map");

		storageService.storeToDisk("121221", "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testEmptyPacketStoreParam() throws Exception {
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.PACKET_STORE_LOCATION, RegistrationConstants.EMPTY);

		//ReflectionTestUtils.setField(StorageServiceImpl.class, "packetStoreLocation", "..//PacketStore");
		
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");

		storageService.storeToDisk("121221", "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testDateFormatParamNotFound() throws Exception {
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.PACKET_STORE_LOCATION, "./PacketStore");

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");

		storageService.storeToDisk("121221", "packet.zip".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testEmptyDateFormatParam() throws Exception {
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.PACKET_STORE_LOCATION, "./PacketStore");
		appMap.put(RegistrationConstants.PACKET_STORE_DATE_FORMAT, RegistrationConstants.EMPTY);

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");

		storageService.storeToDisk("121221", "packet.zip".getBytes());
	}

}
