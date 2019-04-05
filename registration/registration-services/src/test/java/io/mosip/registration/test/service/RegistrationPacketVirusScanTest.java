package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.service.packet.impl.RegistrationPacketVirusScanServiceImpl;

@Ignore
public class RegistrationPacketVirusScanTest {

	@Rule
	public MockitoRule MockitoRule = MockitoJUnit.rule();
	
	@Mock
	private VirusScanner<Boolean, String> virusScanner;
	
	@InjectMocks
	private RegistrationPacketVirusScanServiceImpl registrationPacketVirusScanServiceImpl;
	
	@Before
	public void Initialize() {
		
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.PKT_STORE_LOC, "..//PacketStore");
		appMap.put(RegistrationConstants.CLIENT_PATH, "..//PreRegPacketStore");
		appMap.put(RegistrationConstants.DB_PATH, "..//PreRegPacketStore");
		appMap.put(RegistrationConstants.LOGS_PATH, "..//PreRegPacketStore");
		ApplicationContext.getInstance().setApplicationMap(appMap);
		
		ReflectionTestUtils.setField(registrationPacketVirusScanServiceImpl, "preRegPacketLocation", "..//PreRegPacketStore");
	}
	
	@Test
	public void scanPacket() throws IOException {

		Mockito.when(virusScanner.scanDocument(Mockito.any(File.class))).thenReturn(true);
		assertEquals("Success",registrationPacketVirusScanServiceImpl.scanPacket().getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void scanPacketNegative() throws IOException {
		Mockito.when(virusScanner.scanDocument(Mockito.any(File.class))).thenReturn(false);
		assertNotNull(registrationPacketVirusScanServiceImpl.scanPacket().getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void scanPacketVirusScannerException() throws IOException {
		Mockito.when(virusScanner.scanDocument(Mockito.any(File.class))).thenThrow(new VirusScannerException());
		assertNotNull(registrationPacketVirusScanServiceImpl.scanPacket().getErrorResponseDTOs().get(0));
	}
	
	@Test
	public void scanPacketIOException() throws IOException {
		Mockito.when(virusScanner.scanDocument(Mockito.any(File.class))).thenThrow(new IOException());
		assertNotNull(registrationPacketVirusScanServiceImpl.scanPacket().getErrorResponseDTOs().get(0));
	}
}
