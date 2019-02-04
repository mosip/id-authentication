package io.mosip.registration.test.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import io.mosip.kernel.auditmanager.entity.Audit;
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
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.entity.RegistrationAuditDates;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.impl.PacketCreationServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.util.kernal.cbeff.service.impl.CbeffImpl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
	@Mock
	private AuditLogControlDAO auditLogControlDAO;
	private static RegistrationDTO registrationDTO;
	private RegistrationAuditDates registrationAuditDates;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		SessionContext.getInstance().setMapObject(new HashMap<>());
		registrationDTO = DataProvider.getPacketDTO();
	}

	@Before
	public void intializeForTest() {
		registrationAuditDates = new RegistrationAuditDates() {

			@Override
			public Timestamp getAuditLogFromDateTime() {
				return null;
			}

			@Override
			public Timestamp getAuditLogToDateTime() {
				return null;
			}
		};
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.CBEFF_ONLY_UNIQUE_TAGS,
				RegistrationConstants.GLOBAL_CONFIG_TRUE_VALUE);
		ApplicationContext.getInstance().setApplicationMap(applicationMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePacket() throws Exception {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(null);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(registrationAuditDates);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());

		packetCreationServiceImpl.create(null);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testCBEFFException() throws Exception {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList())).thenThrow(new Exception("Invalid BIR"));
		when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test(expected =  RegBaseCheckedException.class)
	public void testJsonValidationException() throws Exception {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new JsonValidationProcessingException("errorCode", "errorMessage"));

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPacketCreationWithLatestRegistration() throws Exception {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(registrationAuditDates);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());
		ApplicationContext.getInstance().getApplicationMap().put(RegistrationConstants.CBEFF_ONLY_UNIQUE_TAGS, "N");

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPacketCreationWithLatestRegistrationAudits() throws Exception {
		Timestamp auditStartTime = Timestamp.valueOf(LocalDateTime.now().minusDays(1).minusHours(2));
		Timestamp auditEndTime = Timestamp.valueOf(LocalDateTime.now().minusDays(1));

		registrationAuditDates = new RegistrationAuditDates() {
			
			@Override
			public Timestamp getAuditLogFromDateTime() {
				return auditStartTime;
			}
			
			@Override
			public Timestamp getAuditLogToDateTime() {
				return auditEndTime;
			}
		};
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList())).thenReturn("cbeffXML".getBytes());
		when(jsonValidator.validateJson(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ValidationReport());
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(registrationAuditDates);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@AfterClass
	public static void destroy() {
		SessionContext.destroySession();
		ApplicationContext.getInstance().setApplicationMap(null);
	}

	private List<Audit> getAudits() {
		List<Audit> audits = new ArrayList<>();

		addAuditDTOToList(audits, "Capture Demographic Data", "Data Capture", "Caputured demographic data");
		addAuditDTOToList(audits, "Capture Left Iris", "Iris Capture", "Caputured left iris");
		addAuditDTOToList(audits, "Capture Right Iris", "Iris Capture", "Caputured right iris");
		addAuditDTOToList(audits, "Capture Right Palm", "Palm Capture", "Caputured Right Palm");
		addAuditDTOToList(audits, "Capture Left Palm", "Palm Capture", "Caputured Left Palm");
		addAuditDTOToList(audits, "Capture Both Thumb", "Thumbs Capture", "Caputured Both Thumb");

		return audits;
	}

	private void addAuditDTOToList(List<Audit> audits, String eventName, String eventType,
			String description) {
		LocalDateTime dateTime = LocalDateTime.now();

		Audit audit = new Audit();

		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName(eventName);
		audit.setEventType(eventType);
		audit.setActionTimeStamp(dateTime);
		audit.setHostName(RegistrationConstants.LOCALHOST);
		audit.setHostIp(RegistrationConstants.LOCALHOST);
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription(description);
		audits.add(audit);
	}

}
