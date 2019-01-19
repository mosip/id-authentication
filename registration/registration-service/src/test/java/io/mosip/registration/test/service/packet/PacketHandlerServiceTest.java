package io.mosip.registration.test.service.packet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.PacketCreationService;
import io.mosip.registration.service.packet.PacketEncryptionService;
import io.mosip.registration.service.packet.impl.PacketHandlerServiceImpl;

public class PacketHandlerServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PacketHandlerServiceImpl packetHandlerServiceImpl;
	@Mock
	private PacketCreationService packetCreationService;
	@Mock
	private PacketEncryptionService packetEncryptionService;
	@Mock
	private AuditFactoryImpl auditFactory;
	private ResponseDTO mockedSuccessResponse;

	@Before
	public void initialize() {
		mockedSuccessResponse = new ResponseDTO();
	}

	@Test
	public void testHandle() throws RegBaseCheckedException {
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class))).thenReturn("Packet Creation".getBytes());
		Mockito.when(
				packetEncryptionService.encrypt(Mockito.any(RegistrationDTO.class), Mockito.anyString().getBytes()))
				.thenReturn(mockedSuccessResponse);
		Assert.assertSame(mockedSuccessResponse, packetHandlerServiceImpl.handle(new RegistrationDTO()));
	}

	@Test
	public void testCreationException() throws RegBaseCheckedException {
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class))).thenReturn(null);
		ResponseDTO actualResponse = packetHandlerServiceImpl.handle(new RegistrationDTO());
		Assert.assertEquals(RegistrationExceptionConstants.REG_PACKET_CREATION_ERROR_CODE.getErrorCode(),
				actualResponse.getErrorResponseDTOs().get(0).getCode());
	}

	@Test
	public void testHandlerException() throws RegBaseCheckedException {
		RegBaseUncheckedException exception = new RegBaseUncheckedException("errorCode", "errorMsg");
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class)))
				.thenThrow(exception);
		Mockito.when(
				packetEncryptionService.encrypt(Mockito.any(RegistrationDTO.class), Mockito.anyString().getBytes()))
				.thenReturn(mockedSuccessResponse);
		ResponseDTO dto = packetHandlerServiceImpl.handle(new RegistrationDTO());
		Assert.assertNotNull(dto.getErrorResponseDTOs());
	}

	@Test
	public void testHandlerChkException() throws RegBaseCheckedException {
		RegBaseCheckedException exception = new RegBaseCheckedException("errorCode", "errorMsg");
		Mockito.when(packetCreationService.create(Mockito.any(RegistrationDTO.class)))
				.thenThrow(exception);
		Mockito.when(
				packetEncryptionService.encrypt(Mockito.any(RegistrationDTO.class), Mockito.anyString().getBytes()))
				.thenReturn(mockedSuccessResponse);
		ResponseDTO dto = packetHandlerServiceImpl.handle(new RegistrationDTO());
		Assert.assertNotNull(dto.getErrorResponseDTOs());
	}

}
