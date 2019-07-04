package io.mosip.registration.test.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.RegDeviceSpec;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.RegistrationAuditDates;
import io.mosip.registration.entity.id.RegMachineSpecId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.impl.PacketCreationServiceImpl;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.validator.RegIdObjectValidator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class, SessionContext.class })
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
	private AuditManagerSerivceImpl auditFactory;
	@Mock
	private AuditDAO auditDAO;
	@Mock
	private CbeffImpl cbeffI;
	@Mock
	private RegIdObjectValidator idObjectValidator;
	@Mock
	private AuditLogControlDAO auditLogControlDAO;
	@Mock
	private MachineMappingDAO machineMappingDAO;
	private static RegistrationDTO registrationDTO;
	private static RegistrationAuditDates registrationAuditDates;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		registrationDTO = DataProvider.getPacketDTO();
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
	}

	@Before
	public void intializeForTest() throws Exception {
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.CBEFF_UNQ_TAG, RegistrationConstants.GLOBAL_CONFIG_TRUE_VALUE);

		Map<String, Object> sessionMap = new HashMap<>();
		sessionMap.put(RegistrationConstants.IS_Child, true);

		PowerMockito.mockStatic(SessionContext.class, ApplicationContext.class);
		PowerMockito.doReturn(sessionMap).when(SessionContext.class, "map");
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");

		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePacket() throws Exception {
		List<RegDeviceMaster> devices = new ArrayList<>();
		RegDeviceMaster device = new RegDeviceMaster();
		RegMachineSpecId regMachineSpecId = new RegMachineSpecId();
		regMachineSpecId.setId("3309823");
		device.setRegMachineSpecId(regMachineSpecId);
		RegDeviceSpec regDeviceSpec = new RegDeviceSpec();
		RegDeviceType regDeviceType = new RegDeviceType();
		regDeviceType.setName("Fingerprint");
		regDeviceSpec.setRegDeviceType(regDeviceType);
		device.setRegDeviceSpec(regDeviceSpec);
		devices.add(device);

		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList(), Mockito.anyString().getBytes())).thenReturn("cbeffXML".getBytes());
		Mockito.doAnswer((idObject) -> {
			return "Success";
		}).when(idObjectValidator).validateIdObject(Mockito.any(), Mockito.any());
		// when(idObjectValidator.validateIdObject(Mockito.any(),Mockito.any())).thenReturn(true);
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(null);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());
		when(machineMappingDAO.getDevicesMappedToRegCenter(Mockito.anyString())).thenReturn(devices);

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testException() throws RegBaseCheckedException {
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(registrationAuditDates);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());
		when(machineMappingDAO.getDevicesMappedToRegCenter(Mockito.anyString())).thenReturn(new ArrayList<>());

		packetCreationServiceImpl.create(null);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testCBEFFException() throws Exception {
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList(), Mockito.anyString().getBytes()))
				.thenThrow(new Exception("Invalid BIR"));
		Mockito.doAnswer((idObject) -> {
			return "Success";
		}).when(idObjectValidator).validateIdObject(Mockito.any(), Mockito.any());
		when(machineMappingDAO.getDevicesMappedToRegCenter(Mockito.anyString())).thenReturn(new ArrayList<>());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@Ignore
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testJsonValidationException() throws Exception {
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList(), Mockito.anyString().getBytes())).thenReturn("cbeffXML".getBytes());
		doThrow(new RegBaseCheckedException("errorCode", "errorMessage")).when(idObjectValidator).validateIdObject(Mockito.any(), Mockito.any());
		/*
		 * when(idObjectValidator.validateIdObject(Mockito.any(),Mockito.any()))
		 * .thenThrow(new RegBaseCheckedException("errorCode", "errorMessage"));
		 */
		when(machineMappingDAO.getDevicesMappedToRegCenter(Mockito.anyString())).thenReturn(new ArrayList<>());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPacketCreationWithLatestRegistration() throws Exception {
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.CBEFF_UNQ_TAG, RegistrationConstants.DISABLE);

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");

		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList(), Mockito.anyString().getBytes())).thenReturn("cbeffXML".getBytes());
		Mockito.doAnswer((idObject) -> {
			return "Success";
		}).when(idObjectValidator).validateIdObject(Mockito.any(), Mockito.any());
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(registrationAuditDates);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());
		when(machineMappingDAO.getDevicesMappedToRegCenter(Mockito.anyString())).thenReturn(new ArrayList<>());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPacketCreationWithLatestRegistrationAudits() throws Exception {
		Timestamp auditStartTime = Timestamp.valueOf(LocalDateTime.now().minusDays(1).minusHours(2));
		Timestamp auditEndTime = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
		RegistrationAuditDates registrationAuditDates = new RegistrationAuditDates() {

			@Override
			public Timestamp getAuditLogFromDateTime() {
				return auditStartTime;
			}

			@Override
			public Timestamp getAuditLogToDateTime() {
				return auditEndTime;
			}
		};

		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList(), Mockito.anyString().getBytes())).thenReturn("cbeffXML".getBytes());
		Mockito.doAnswer((idObject) -> {
			return "Success";
		}).when(idObjectValidator).validateIdObject(Mockito.any(), Mockito.any());
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenReturn(registrationAuditDates);
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());

		Assert.assertNotNull(packetCreationServiceImpl.create(registrationDTO));
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

	private void addAuditDTOToList(List<Audit> audits, String eventName, String eventType, String description) {
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
