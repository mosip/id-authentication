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
import java.util.WeakHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.springframework.test.util.ReflectionTestUtils;

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
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
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
		PowerMockito.doReturn("eng").when(ApplicationContext.class, "applicationLanguage");

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
		when(auditLogControlDAO.getLatestRegistrationAuditDates()).thenThrow(new NullPointerException("date"));
		when(auditDAO.getAudits(Mockito.any(RegistrationAuditDates.class))).thenReturn(getAudits());
		when(machineMappingDAO.getDevicesMappedToRegCenter(Mockito.anyString())).thenReturn(new ArrayList<>());

		packetCreationServiceImpl.create(registrationDTO);
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

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void testJsonValidationException() throws Exception {
		when(zipCreationService.createPacket(Mockito.any(RegistrationDTO.class), Mockito.anyMap()))
				.thenReturn("zip".getBytes());
		when(cbeffI.createXML(Mockito.anyList(), Mockito.anyString().getBytes())).thenReturn("cbeffXML".getBytes());
		doThrow(new RegBaseCheckedException("errorCode", "errorMessage")).when(idObjectValidator)
				.validateIdObject(Mockito.any(), Mockito.any());
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
		PowerMockito.doReturn("eng").when(ApplicationContext.class, "applicationLanguage");

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

	@Test(expected = RegBaseCheckedException.class)
	public void testNullRegistration() throws Throwable {
		try {
			RegistrationDTO registrationDTO = null;
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationDTO", registrationDTO);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidRID() throws Throwable {
		try {
			Map<String, Object> appMap = new HashMap<>();
			appMap.put(RegistrationConstants.CBEFF_UNQ_TAG, RegistrationConstants.GLOBAL_CONFIG_TRUE_VALUE);

			PowerMockito.mockStatic(ApplicationContext.class);
			PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
			PowerMockito.doReturn("eng").when(ApplicationContext.class, "applicationLanguage");

			RegistrationDTO registrationDTO = new RegistrationDTO();
			registrationDTO.setRegistrationId(RegistrationConstants.EMPTY);
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationDTO", registrationDTO);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testFingerPrintDisabled() throws Throwable {
		try {
			Map<String, Object> appMap = new HashMap<>();
			appMap.put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, RegistrationConstants.DISABLE);
			appMap.put(RegistrationConstants.IRIS_DISABLE_FLAG, RegistrationConstants.ENABLE);
			appMap.put(RegistrationConstants.FACE_DISABLE_FLAG, RegistrationConstants.DISABLE);

			PowerMockito.mockStatic(ApplicationContext.class);
			PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
			PowerMockito.doReturn("eng").when(ApplicationContext.class, "applicationLanguage");

			RegistrationDTO registrationDTO = new RegistrationDTO();
			registrationDTO.setRegistrationId("781716175165137614615186");
			RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
			metaData.setCenterId("10001");
			metaData.setMachineId("10001");
			metaData.setDeviceId("10001");
			metaData.setConsentOfApplicant("Yes");
			metaData.setRegistrationCategory("New");
			registrationDTO.setRegistrationMetaDataDTO(metaData);
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationDTO", registrationDTO);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testIrisDisabled() throws Throwable {
		try {
			Map<String, Object> appMap = new HashMap<>();
			appMap.put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, RegistrationConstants.ENABLE);
			appMap.put(RegistrationConstants.IRIS_DISABLE_FLAG, RegistrationConstants.DISABLE);
			appMap.put(RegistrationConstants.FACE_DISABLE_FLAG, RegistrationConstants.ENABLE);

			PowerMockito.mockStatic(ApplicationContext.class);
			PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
			PowerMockito.doReturn("eng").when(ApplicationContext.class, "applicationLanguage");

			RegistrationDTO registrationDTO = new RegistrationDTO();
			registrationDTO.setRegistrationId("781716175165137614615186");
			RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
			metaData.setCenterId("10001");
			metaData.setMachineId("10001");
			metaData.setDeviceId("10001");
			metaData.setConsentOfApplicant("Yes");
			metaData.setRegistrationCategory("New");
			registrationDTO.setRegistrationMetaDataDTO(metaData);
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationDTO", registrationDTO);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test
	public void testValidMetaData() throws Throwable {
		RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
		metaData.setCenterId("10001");
		metaData.setMachineId("10001");
		metaData.setDeviceId("10001");
		metaData.setConsentOfApplicant("Yes");
		metaData.setRegistrationCategory("New");
		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationMetaData", metaData);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testNullMetaData() throws Throwable {
		try {
			RegistrationMetaDataDTO metaData = null;
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationMetaData", metaData);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidRegistrationCategoryMetaData() throws Throwable {
		try {
			RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationMetaData", metaData);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidRegistrationCenterMetaData() throws Throwable {
		try {
			RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
			metaData.setRegistrationCategory("New");
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationMetaData", metaData);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidRegistrationMachineMetaData() throws Throwable {
		try {
			RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
			metaData.setRegistrationCategory("New");
			metaData.setCenterId("10001");
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationMetaData", metaData);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidApplicantConsentMetaData() throws Throwable {
		try {
			RegistrationMetaDataDTO metaData = new RegistrationMetaDataDTO();
			metaData.setRegistrationCategory("New");
			metaData.setCenterId("10001");
			metaData.setMachineId("10011");
			metaData.setDeviceId("10001");
			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateRegistrationMetaData", metaData);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testNullOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidOfficerIdOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			OSIDataDTO osiData = new OSIDataDTO();
			registration.setOsiDataDTO(osiData);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidOfficerAuthOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			OSIDataDTO osiData = new OSIDataDTO();
			osiData.setOperatorID("10001");
			registration.setOsiDataDTO(osiData);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testOfficerAuthByPasswordOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			OSIDataDTO osiData = new OSIDataDTO();
			osiData.setOperatorID("10001");
			osiData.setOperatorAuthenticatedByPassword(true);
			registration.setOsiDataDTO(osiData);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
			biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testOfficerAuthByPINOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			OSIDataDTO osiData = new OSIDataDTO();
			osiData.setOperatorID("10001");
			osiData.setOperatorAuthenticatedByPIN(true);
			registration.setOsiDataDTO(osiData);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
			biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testOfficerAuthByBiometricsOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			OSIDataDTO osiData = new OSIDataDTO();
			osiData.setOperatorID("10001");
			registration.setOsiDataDTO(osiData);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
			biometricsCapturedFlags.put(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED, true);
			biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidSupervisorAuthOSIData() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			OSIDataDTO osiData = new OSIDataDTO();
			osiData.setOperatorID("10001");
			osiData.setSupervisorID("10001");
			registration.setOsiDataDTO(osiData);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
			biometricsCapturedFlags.put(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED, true);
			biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
					biometricsCapturedFlags);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test
	public void testSupervisorAuthByPasswordOSIData() throws Throwable {
		RegistrationDTO registration = new RegistrationDTO();
		OSIDataDTO osiData = new OSIDataDTO();
		osiData.setOperatorID("10001");
		osiData.setSupervisorID("10001");
		osiData.setSuperviorAuthenticatedByPassword(true);
		registration.setOsiDataDTO(osiData);
		Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
		biometricsCapturedFlags.put(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED, true);
		biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
				biometricsCapturedFlags);
	}

	@Test
	public void testSupervisorAuthByPinOSIData() throws Throwable {
		RegistrationDTO registration = new RegistrationDTO();
		OSIDataDTO osiData = new OSIDataDTO();
		osiData.setOperatorID("10001");
		osiData.setSupervisorID("10001");
		osiData.setSuperviorAuthenticatedByPIN(true);
		registration.setOsiDataDTO(osiData);
		Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
		biometricsCapturedFlags.put(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED, true);
		biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
				biometricsCapturedFlags);
	}

	@Test
	public void testSupervisorAuthByBiometricsOSIData() throws Throwable {
		RegistrationDTO registration = new RegistrationDTO();
		OSIDataDTO osiData = new OSIDataDTO();
		osiData.setOperatorID("10001");
		osiData.setSupervisorID("10001");
		registration.setOsiDataDTO(osiData);
		Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();
		biometricsCapturedFlags.put(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED, true);
		biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, true);
		biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_BIOMETRICS_CAPTURED, true);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateOSIData", registration,
				biometricsCapturedFlags);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidApplicantBiometrics() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			BiometricDTO biometrics = new BiometricDTO();
			registration.setBiometricDTO(biometrics);

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateBiometrics", registration,
					true, true);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testMissingFace() throws Throwable {
		try {
			RegistrationDTO registration = new RegistrationDTO();
			BiometricDTO biometrics = new BiometricDTO();
			BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
			List<BiometricExceptionDTO> biometricExceptions = new ArrayList<>();
			BiometricExceptionDTO biometricsException = new BiometricExceptionDTO();
			biometricExceptions.add(biometricsException);
			applicantBiometrics.setBiometricExceptionDTO(biometricExceptions);
			biometrics.setApplicantBiometricDTO(applicantBiometrics);
			biometrics.setIntroducerBiometricDTO(applicantBiometrics);
			registration.setBiometricDTO(biometrics);

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateBiometrics", registration,
					true, true);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test
	public void testBiometrics() throws Throwable {
		RegistrationDTO registration = new RegistrationDTO();
		BiometricDTO biometrics = new BiometricDTO();
		BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
		List<BiometricExceptionDTO> biometricExceptions = new ArrayList<>();
		BiometricExceptionDTO biometricsException = new BiometricExceptionDTO();
		biometricExceptions.add(biometricsException);
		applicantBiometrics.setBiometricExceptionDTO(biometricExceptions);
		FaceDetailsDTO face = new FaceDetailsDTO();
		face.setFace("face".getBytes());
		applicantBiometrics.setFace(face);
		biometrics.setApplicantBiometricDTO(applicantBiometrics);
		biometrics.setIntroducerBiometricDTO(applicantBiometrics);
		registration.setBiometricDTO(biometrics);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateBiometrics", registration, false, true);
	}

	@Test
	public void testWithoutIntroducerBiometrics() throws Throwable {
		RegistrationDTO registration = new RegistrationDTO();
		BiometricDTO biometrics = new BiometricDTO();
		BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
		List<BiometricExceptionDTO> biometricExceptions = new ArrayList<>();
		BiometricExceptionDTO biometricsException = new BiometricExceptionDTO();
		biometricExceptions.add(biometricsException);
		applicantBiometrics.setBiometricExceptionDTO(biometricExceptions);
		biometrics.setApplicantBiometricDTO(applicantBiometrics);
		registration.setBiometricDTO(biometrics);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateBiometrics", registration, true, false);
	}

	@Test
	public void testEmptyApplicantBiometrics() throws Throwable {
		RegistrationDTO registration = new RegistrationDTO();
		BiometricDTO biometrics = new BiometricDTO();
		BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
		List<BiometricExceptionDTO> biometricExceptions = new ArrayList<>();
		BiometricExceptionDTO biometricsException = new BiometricExceptionDTO();
		biometricExceptions.add(biometricsException);
		applicantBiometrics.setBiometricExceptionDTO(biometricExceptions);
		biometrics.setIntroducerBiometricDTO(applicantBiometrics);
		biometrics.setApplicantBiometricDTO(new BiometricInfoDTO());
		registration.setBiometricDTO(biometrics);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateBiometrics", registration, true, false);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testMissingExceptionFace() throws Throwable {
		try {
			BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
			FaceDetailsDTO face = new FaceDetailsDTO();
			face.setFace("face".getBytes());
			applicantBiometrics.setFace(face);
			BiometricInfoDTO authenticationBiometrics = new BiometricInfoDTO();

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateFace", true, applicantBiometrics, authenticationBiometrics,
					true, true);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testMissingAuthenticationExceptionFace() throws Throwable {
		try {
			BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
			FaceDetailsDTO face = new FaceDetailsDTO();
			face.setFace("face".getBytes());
			applicantBiometrics.setFace(face);
			BiometricInfoDTO authenticationBiometrics = new BiometricInfoDTO();
			authenticationBiometrics.setExceptionFace(new FaceDetailsDTO());

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateFace", true, applicantBiometrics, authenticationBiometrics,
					false, true);
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test
	public void testExceptionFaces() throws Throwable {
		BiometricInfoDTO applicantBiometrics = new BiometricInfoDTO();
		FaceDetailsDTO face = new FaceDetailsDTO();
		face.setFace("face".getBytes());
		applicantBiometrics.setFace(face);
		applicantBiometrics.setExceptionFace(face);
		BiometricInfoDTO authenticationBiometrics = new BiometricInfoDTO();
		authenticationBiometrics.setExceptionFace(face);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateFace", true, applicantBiometrics,
				authenticationBiometrics, true, true);
	}

	@Test
	public void testBiometricCaptured() throws Throwable {
		BiometricInfoDTO biometrics = new BiometricInfoDTO();
		FaceDetailsDTO face = new FaceDetailsDTO();
		face.setFace("face".getBytes());
		biometrics.setFace(face);
		biometrics.setFingerprintDetailsDTO(new ArrayList<>());
		List<IrisDetailsDTO> iris = new ArrayList<>();
		iris.add(new IrisDetailsDTO());
		biometrics.setIrisDetailsDTO(iris);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "isBiometricCaptured", biometrics);
	}

	@Test
	public void testWithoutBiometrics() throws Throwable {
		BiometricInfoDTO biometrics = new BiometricInfoDTO();

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "isBiometricCaptured", biometrics);
	}

	@Test
	public void testBiometricsWithoutFace() throws Throwable {
		BiometricInfoDTO biometrics = new BiometricInfoDTO();
		List<FingerprintDetailsDTO> fingers = new ArrayList<>();
		fingers.add(new FingerprintDetailsDTO());
		biometrics.setFingerprintDetailsDTO(fingers);
		List<IrisDetailsDTO> iris = new ArrayList<>();
		iris.add(new IrisDetailsDTO());
		biometrics.setIrisDetailsDTO(iris);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "isBiometricCaptured", biometrics);
	}

	@Test
	public void testAllBiometrics() throws Throwable {
		BiometricInfoDTO biometrics = new BiometricInfoDTO();
		List<FingerprintDetailsDTO> fingers = new ArrayList<>();
		fingers.add(new FingerprintDetailsDTO());
		biometrics.setFingerprintDetailsDTO(fingers);
		FaceDetailsDTO face= new FaceDetailsDTO();
		face.setFace("face".getBytes());
		biometrics.setFace(face);

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "isBiometricCaptured", biometrics);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testNullSessionContextMap() throws Throwable {
		try {
			PowerMockito.mockStatic(SessionContext.class);
			PowerMockito.doReturn(null).when(SessionContext.class, "map");

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateContexts");
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testNullApplicationContextMap() throws Throwable {
		try {
			PowerMockito.mockStatic(ApplicationContext.class);
			PowerMockito.doReturn(null).when(ApplicationContext.class, "map");

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateContexts");
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidApplicationLanguage() throws Throwable {
		try {
			PowerMockito.mockStatic(ApplicationContext.class);
			PowerMockito.doReturn(new HashMap<>()).when(ApplicationContext.class, "map");
			PowerMockito.doReturn(RegistrationConstants.EMPTY).when(ApplicationContext.class, "applicationLanguage");

			ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateContexts");
		} catch (RuntimeException exception) {
			throw exception.getCause();
		}
	}

	@Test
	public void testNoCBEFFTagProperty() throws Throwable {
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(new HashMap<>()).when(ApplicationContext.class, "map");
		PowerMockito.doReturn(RegistrationConstants.ENGLISH_LANG_CODE).when(ApplicationContext.class,
				"applicationLanguage");

		ReflectionTestUtils.invokeMethod(packetCreationServiceImpl, "validateContexts");
	}

}
