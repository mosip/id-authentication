package io.mosip.registration.test.service.packet.encryption;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.AESEncryptionService;
import io.mosip.registration.service.external.StorageService;
import io.mosip.registration.service.packet.impl.PacketEncryptionServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
public class PacketEncryptionServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PacketEncryptionServiceImpl packetEncryptionServiceImpl;
	@Mock
	private AESEncryptionService aesEncryptionManager;
	@Mock
	private StorageService storageService;
	@Mock
	private RegistrationDAO registrationDAO;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Mock
	private AuditDAO auditDAO;
	private RegistrationDTO registrationDTO;

	@Before
	public void initialize() throws IOException, URISyntaxException, RegBaseCheckedException {
		
		registrationDTO = DataProvider.getPacketDTO();

		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.when(ApplicationContext.getInstance()).thenReturn(applicationContext);
		
		Map<String, Object> globalParams = new HashMap<>();
		globalParams.put("MAX_REG_PACKET_SIZE", "1");
		PowerMockito.when(applicationContext.getApplicationMap()).thenReturn(globalParams);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEncryption() throws RegBaseCheckedException {
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("Encrypted_Data".getBytes());
		when(storageService.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		when(auditDAO.updateSyncAudits(Mockito.anyList())).thenReturn(2);
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		ResponseDTO responseDTO = packetEncryptionServiceImpl.encrypt(registrationDTO, "PacketZip".getBytes());
		Assert.assertEquals("0000", responseDTO.getSuccessResponseDTO().getCode());
		Assert.assertEquals("Success", responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testCheckedException() throws RegBaseCheckedException {
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenThrow(RegBaseCheckedException.class);
		when(storageService.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		when(auditDAO.updateSyncAudits(Mockito.anyList())).thenReturn(2);
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		packetEncryptionServiceImpl.encrypt(registrationDTO, "PacketZip".getBytes());
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testUncheckedException() throws RegBaseCheckedException {
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("Encrypted_Data".getBytes());
		when(storageService.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		when(auditDAO.updateSyncAudits(Mockito.anyList())).thenReturn(2);
		packetEncryptionServiceImpl.encrypt(new RegistrationDTO(), "PacketZip".getBytes());
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void packetSizeExceededTets() throws RegBaseCheckedException {
		byte[] encryptedData = new byte[1050576];
		when(aesEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn(encryptedData);
		when(storageService.storeToDisk(Mockito.anyString(), Mockito.anyString().getBytes())).thenReturn("D:/Packet Store/27-Sep-2018/1111_Ack.jpg");
		when(auditDAO.updateSyncAudits(Mockito.anyList())).thenReturn(2);
		doNothing().when(registrationDAO).save(Mockito.anyString(), Mockito.anyString());
		packetEncryptionServiceImpl.encrypt(registrationDTO, "PacketZip".getBytes());
	}

}
