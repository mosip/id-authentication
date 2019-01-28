package io.mosip.registration.test.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.impl.PacketCreationServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.util.kernal.cbeff.service.impl.CbeffImpl;

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
	@Mock
	private CbeffImpl cbeffI;
	@Mock
	private JsonValidator jsonValidator;
	private static RegistrationDTO registrationDTO;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		SessionContext.getInstance().setMapObject(new HashMap<>());
		registrationDTO = DataProvider.getPacketDTO();
	}

	@Before
	public void intializeForTest() {
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.CBEFF_ONLY_UNIQUE_TAGS,
				RegistrationConstants.GLOBAL_CONFIG_TRUE_VALUE);
		ApplicationContext.getInstance().setApplicationMap(applicationMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePacket() throws Exception {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		Mockito.when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		packetCreationServiceImpl.create(null);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testCBEFFException() throws Exception {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		Mockito.when(cbeffI.createXML(Mockito.anyList())).thenThrow(new Exception("Invalid BIR"));
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test(expected =  RegBaseCheckedException.class)
	public void testJsonValidationException() throws Exception {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		Mockito.when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new JsonValidationProcessingException("errorCode", "errorMessage"));

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePacketWithTestTags() throws Exception {
		Mockito.doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		Mockito.when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());
		ApplicationContext.getInstance().getApplicationMap().put(RegistrationConstants.CBEFF_ONLY_UNIQUE_TAGS, "N");

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@AfterClass
	public static void destroy() {
		SessionContext.destroySession();
		ApplicationContext.getInstance().setApplicationMap(null);
	}

}
