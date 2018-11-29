package io.mosip.registration.test.service;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.impl.StorageServiceImpl;

public class StorageServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private StorageServiceImpl storageService;
	@Mock
	private Environment environment;
	
	@Before
	public void initialize() {
		ReflectionTestUtils.setField(storageService, "environment", environment);
		
		when(environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION)).thenReturn("PacketStore");
		when(environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT)).thenReturn("dd-MMM-yyyy");
	}
	
	@Test
	public void testLocalStorage() throws RegBaseCheckedException {
		Assert.assertNotNull(storageService.storeToDisk("1234567890123", "demo".getBytes(), "Image".getBytes()));		
	}
	
	@Test(expected=RegBaseUncheckedException.class)
	public void testIOException() throws RegBaseCheckedException {
		storageService.storeToDisk(null, "packet.zip".getBytes(), "ackReceipt".getBytes());
	}
}
