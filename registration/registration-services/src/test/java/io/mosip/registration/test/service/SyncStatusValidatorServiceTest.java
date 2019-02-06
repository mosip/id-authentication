package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncJobControlDAO.SyncJobInfo;
import io.mosip.registration.device.gps.GPSFacade;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.sync.impl.SyncStatusValidatorServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
public class SyncStatusValidatorServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private SyncStatusValidatorServiceImpl syncStatusValidatorServiceImpl;
	@Mock
	private SyncJobControlDAO syncJobDAO;
	@Mock
	private SyncJobInfo syncJobInfo;
	@Mock
	private SyncJobConfigDAO jobConfigDAO;
	@Mock
	private GPSFacade gpsFacade;
	@Mock
	io.mosip.registration.context.ApplicationContext context;
	@Mock
	private AuditFactory auditFactory;

	@BeforeClass
	public static void beforeClass() {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
	}

	@Before
	public void initialize() throws IOException, URISyntaxException {
		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "N");
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterLatitude("12.991276");
		centerDetailDTO.setRegistrationCenterLongitude("80.2461");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);

		Instant lastCapturedTime = null;
		Map<String, Object> maplastTime = new HashMap<>();
		maplastTime.put("lastCapturedTime", lastCapturedTime);
		SessionContext.getInstance().setMapObject(maplastTime);

		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		PowerMockito.mockStatic(ApplicationContext.class);
	}

	@Test
	public void testValidateSyncStatusFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "Y");

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 550.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "10");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "100");
		applicationMap.put("GEO_CAP_FREQ", "Y");
		applicationMap.put("masterSyncJob", "1");
		applicationMap.put("lastExportSyncJob", "1");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);
		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(1).getCode());
		assertEquals(
				"Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",
				errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals(
				"Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-004", errorResponseDTOs.get(3).getCode());
		assertEquals(
				"Your client machine location is outside the registration center. Please note that registration can be done only from within the registration centre",
				errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateSyncStatusSuccessCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "N");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 55.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "100");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "215");
		applicationMap.put("GEO_CAP_FREQ", "N");
		applicationMap.put("masterSyncJob", "20");
		applicationMap.put("lastExportSyncJob", "20");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
		assertTrue(errorResponseDTOs.isEmpty());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "Y");

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_FAILURE_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "10");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "100");
		applicationMap.put("GEO_CAP_FREQ", "Y");
		applicationMap.put("masterSyncJob", "1");
		applicationMap.put("lastExportSyncJob", "1");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(1).getCode());
		assertEquals(
				"Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",
				errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals(
				"Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-006", errorResponseDTOs.get(3).getCode());
		assertEquals("Unable to validate machine location due to weak GPS signal. Please try again",
				errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase1() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "Y");

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "10");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "100");
		applicationMap.put("GEO_CAP_FREQ", "Y");
		applicationMap.put("masterSyncJob", "1");
		applicationMap.put("lastExportSyncJob", "1");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(1).getCode());
		assertEquals(
				"Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",
				errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals(
				"Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-005", errorResponseDTOs.get(3).getCode());
		assertEquals("Unable to validate machine location. Please insert the GPS device and try again",
				errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase2() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "Y");

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG,
				RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE_ERRO_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "10");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "100");
		applicationMap.put("GEO_CAP_FREQ", "Y");
		applicationMap.put("masterSyncJob", "1");
		applicationMap.put("lastExportSyncJob", "1");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(1).getCode());
		assertEquals(
				"Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",
				errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals(
				"Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-005", errorResponseDTOs.get(3).getCode());
		assertEquals("Unable to validate machine location. Please insert the GPS device and try again",
				errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase3() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, "");

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "Y");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "10");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "100");
		applicationMap.put("GEO_CAP_FREQ", "Y");
		applicationMap.put("masterSyncJob", "1");
		applicationMap.put("lastExportSyncJob", "1");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(1).getCode());
		assertEquals(
				"Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",
				errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals(
				"Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",
				errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-007", errorResponseDTOs.get(3).getCode());
		assertEquals(RegistrationConstants.OPT_TO_REG_GPS_PORT_MISMATCH, errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidatePacketCountFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "N");

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		Registration registration1 = new Registration();
		registration1.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);
		registrationList.add(registration1);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 55.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "100");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "215");
		applicationMap.put("GEO_CAP_FREQ", "N");
		applicationMap.put("masterSyncJob", "20");
		applicationMap.put("lastExportSyncJob", "20");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "1");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
		assertEquals("REG-ICS‌-008", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Maximum number of registration packets pending approval on client reached. Please approve or reject packets before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());

	}

	@Test
	public void testValidatePacketDurationFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		SyncControl syncControl2 = new SyncControl();
		syncControl2.setSyncJobId("LER_J00009");
		syncControl2.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		ReflectionTestUtils.setField(syncStatusValidatorServiceImpl, "gpsEnableFlag", "N");

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 55.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");
		SyncJobDef syncJobDef2 = new SyncJobDef();
		syncJobDef2.setId("LER_J00009");
		syncJobDef2.setApiName("lastExportSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);
		listSyncJob.add(syncJobDef2);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("REG_PAK_MAX_CNT_OFFLINE_FREQ", "100");
		applicationMap.put("DIST_FRM_MACHN_TO_CENTER", "215");
		applicationMap.put("GEO_CAP_FREQ", "N");
		applicationMap.put("masterSyncJob", "20");
		applicationMap.put("lastExportSyncJob", "20");
		applicationMap.put("REG_PAK_MAX_CNT_APPRV_LIMIT", "5");
		applicationMap.put("REG_PAK_MAX_TIME_APPRV_LIMIT", "1");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
		assertEquals("REG-ICS‌-009", errorResponseDTOs.get(0).getCode());
		assertEquals(
				"Maximum duration for registration packets pending approval on client reached.Please approve or reject packets before proceeding with this registration",
				errorResponseDTOs.get(0).getMessage());

	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);

		when(syncJobDAO.getSyncStatus()).thenThrow(RegBaseUncheckedException.class);
		syncStatusValidatorServiceImpl.validateSyncStatus();
	}

}
