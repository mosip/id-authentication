package io.mosip.registration.test.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.registration.test.util.datastub.DataProvider;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.impl.PacketCreationServiceImpl;
import io.mosip.registration.util.hmac.HMACGeneration;

public class PacketCreationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private PacketCreationServiceImpl packetCreationServiceImpl;
	@Mock
	private ZipCreationService zipCreationService;
	@Mock
	private HMACGeneration hMACGeneration;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Mock
	private AuditDAO auditDAO;
	private static RegistrationDTO registrationDTO;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		registrationDTO = DataProvider.getPacketDTO();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePacket() throws RegBaseCheckedException, IOException, URISyntaxException {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		packetCreationServiceImpl.create(null);
	}

}
