package io.mosip.registrationprocessor.stages.demodedupe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupeAuthentication;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
@PrepareForTest({ IOUtils.class, HMACUtils.class })
public class DemoDedupeAuthenticationTest {
	
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;
	
	@Mock
	private InputStream inputStream;
	
	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;
	
	@Mock
	AuthResponseDTO authResponseDTO = new AuthResponseDTO();
	
	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;
	
	@InjectMocks
	private DemoDedupeAuthentication demoDedupeAuthentication;
	
	@Before
	public void setUp() throws Exception {
		
		List<String> fingers = new ArrayList<>();
		fingers.add("LEFTTHUMB");
		fingers.add("RIGHTRING");
		
		List<String> iris = new ArrayList<>();
		iris.add("LEFTEYE");
		iris.add("RIGHTEYE");
		
		Mockito.when(packetInfoManager.getApplicantFingerPrintImageNameById(anyString())).thenReturn(fingers);
		Mockito.when(packetInfoManager.getApplicantIrisImageNameById(anyString())).thenReturn(iris);
		
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		
		byte[] data = "1234567890".getBytes();
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);
		
		authResponseDTO.setStatus("y");
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(authResponseDTO);
	}
	
	@Test
	public void testDemoDedupeAutheticationSucess() throws ApisResourceAccessException, IOException {
		
		String regId = "1234567890";
		
		List<String> duplicateIds = new ArrayList<>();
		duplicateIds.add("123456789");
		duplicateIds.add("987654321");
		
		boolean result = demoDedupeAuthentication.authenticateDuplicates(regId, duplicateIds);
		
		assertTrue(result);
	}
	
	@Test
	public void testDemoDedupeAutheticationFailure() throws ApisResourceAccessException, IOException {
		
		String regId = "1234567890";
		
		List<String> duplicateIds = new ArrayList<>();
		duplicateIds.add("123456789");
		duplicateIds.add("987654321");
		
		authResponseDTO.setStatus("n");
		
		boolean result = demoDedupeAuthentication.authenticateDuplicates(regId, duplicateIds);
		
		assertFalse(result);
	}

}
