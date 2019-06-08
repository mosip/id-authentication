package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.dao.SyncJobControlDAO;
import io.mosip.registration.dao.SyncJobControlDAO.SyncJobInfo;
import io.mosip.registration.device.gps.GPSFacade;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.impl.SyncStatusValidatorServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ io.mosip.registration.context.ApplicationContext.class, SessionContext.class })
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
	private GlobalParamDAO globalParamDAO;
	@Mock
	private SyncJobConfigDAO jobConfigDAO;
	@Mock
	private GPSFacade gpsFacade;
	@Mock
	private GlobalParamService globalParamService;
	@Mock
	io.mosip.registration.context.ApplicationContext context;
	@Mock
	private AuditManagerService auditFactory;

	@BeforeClass
	public static void beforeClass() {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
	}

	@Before
	public void initialize() throws Exception {
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterLatitude("12.991276");
		centerDetailDTO.setRegistrationCenterLongitude("80.2461");
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getRegistrationCenterDetailDTO()).thenReturn(centerDetailDTO);
		SessionContext.map().put("lastCapturedTime", null);		

		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());

		PowerMockito.mockStatic(ApplicationContext.class);
	}

	@Test
	public void testValidateSyncStatusFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 550.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "10");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "100");
		applicationMap.put("mosip.registration.geo.capture.frequency", "Y");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "1");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "1");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "Y");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "Y");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "0");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);
		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertNotNull(errorResponseDTOs);
		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(1).getCode());
		assertEquals("OPT_TO_REG_TIME_EXPORT_EXCEED", errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(0).getCode());
		assertEquals("OPT_TO_REG_TIME_SYNC_EXCEED", errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals("OPT_TO_REG_REACH_MAX_LIMIT", errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-004", errorResponseDTOs.get(3).getCode());
		assertEquals("OPT_TO_REG_OUTSIDE_LOCATION", errorResponseDTOs.get(3).getMessage());
		assertEquals("REG-REC‌-007", errorResponseDTOs.get(4).getCode());
		assertEquals("OPT_TO_REG_LAST_SOFTWAREUPDATE_CHECK", errorResponseDTOs.get(4).getMessage());

	}

	@Test
	public void testValidateSyncStatusSuccessCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 55.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "100");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "215");
		applicationMap.put("mosip.registration.geo.capture.frequency", "N");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "20");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "20");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "N");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");
		when(ApplicationContext.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
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

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_FAILURE_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "10");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "100");
		applicationMap.put("mosip.registration.geo.capture.frequency", "Y");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "1");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "1");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "Y");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");

		when(context.map()).thenReturn(applicationMap);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);
		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertNotNull(errorResponseDTOs);
		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(1).getCode());
		assertEquals("OPT_TO_REG_TIME_EXPORT_EXCEED", errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(0).getCode());
		assertEquals("OPT_TO_REG_TIME_SYNC_EXCEED", errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals("OPT_TO_REG_REACH_MAX_LIMIT", errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-006", errorResponseDTOs.get(3).getCode());
		assertEquals("OPT_TO_REG_WEAK_GPS", errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase1() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_DEVICE_CONNECTION_FAILURE);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "10");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "100");
		applicationMap.put("mosip.registration.geo.capture.frequency", "Y");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "1");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "1");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "Y");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");

		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertNotNull(errorResponseDTOs);
		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(1).getCode());
		assertEquals("OPT_TO_REG_TIME_EXPORT_EXCEED", errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(0).getCode());
		assertEquals("OPT_TO_REG_TIME_SYNC_EXCEED", errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals("OPT_TO_REG_REACH_MAX_LIMIT", errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-005", errorResponseDTOs.get(3).getCode());
		assertEquals("OPT_TO_REG_INSERT_GPS", errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase2() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
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

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "10");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "100");
		applicationMap.put("mosip.registration.geo.capture.frequency", "Y");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "1");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "1");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "Y");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");

		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertNotNull(errorResponseDTOs);
		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(1).getCode());
		assertEquals("OPT_TO_REG_TIME_EXPORT_EXCEED", errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(0).getCode());
		assertEquals("OPT_TO_REG_TIME_SYNC_EXCEED", errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals("OPT_TO_REG_REACH_MAX_LIMIT", errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-005", errorResponseDTOs.get(3).getCode());
		assertEquals("OPT_TO_REG_INSERT_GPS", errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidateGpsSyncStatusFailureCase3() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put(RegistrationConstants.GPS_DISTANCE, 0.0);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, "");

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "10");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "100");
		applicationMap.put("mosip.registration.geo.capture.frequency", "Y");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "1");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "1");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "Y");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");

		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertNotNull(errorResponseDTOs);
		assertEquals("REG-ICS‌-002", errorResponseDTOs.get(1).getCode());
		assertEquals("OPT_TO_REG_TIME_EXPORT_EXCEED", errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-001", errorResponseDTOs.get(0).getCode());
		assertEquals("OPT_TO_REG_TIME_SYNC_EXCEED", errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(2).getCode());
		assertEquals("OPT_TO_REG_REACH_MAX_LIMIT", errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-007", errorResponseDTOs.get(3).getCode());
		assertEquals(RegistrationConstants.OPT_TO_REG_GPS_PORT_MISMATCH, errorResponseDTOs.get(3).getMessage());

	}

	@Test
	public void testValidatePacketCountFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		Registration registration1 = new Registration();
		registration1.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration1.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
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

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "100");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "215");
		applicationMap.put("mosip.registration.geo.capture.frequency", "N");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "20");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "20");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "1");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "N");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
		assertEquals("REG-ICS‌-008", errorResponseDTOs.get(0).getCode());
		assertEquals("REG_PKT_APPRVL_CNT_EXCEED", errorResponseDTOs.get(0).getMessage());

	}

	@Test
	public void testValidatePacketDurationFailureCase() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 55.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "100");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "215");
		applicationMap.put("mosip.registration.geo.capture.frequency", "N");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "20");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "20");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "0");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "N");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");

		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobInfo.getLastExportRegistrationList()).thenReturn(registrationList);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();
		assertEquals("REG-ICS‌-009", errorResponseDTOs.get(0).getCode());
		assertEquals("REG_PKT_APPRVL_TIME_EXCEED", errorResponseDTOs.get(0).getMessage());

	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);

		when(syncJobDAO.getSyncStatus()).thenThrow(RegBaseUncheckedException.class);
		syncStatusValidatorServiceImpl.validateSyncStatus();
	}

	@Test
	public void testValidateSyncJobFailure() {
		SyncControl syncControl1 = new SyncControl();
		syncControl1.setSyncJobId("MDS_J00001");
		syncControl1.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		List<SyncControl> listSync = new ArrayList<>();
		listSync.add(syncControl1);

		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setCrDtime(new Timestamp(System.currentTimeMillis()));
		registration.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
		registrationList.add(registration);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put(RegistrationConstants.GPS_DISTANCE, 55.9);
		map.put(RegistrationConstants.GPS_CAPTURE_ERROR_MSG, RegistrationConstants.GPS_CAPTURE_SUCCESS_MSG);

		SyncJobDef syncJobDef1 = new SyncJobDef();
		syncJobDef1.setId("MDS_J00001");
		syncJobDef1.setApiName("masterSyncJob");

		List<SyncJobDef> listSyncJob = new ArrayList<>();
		listSyncJob.add(syncJobDef1);

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setUpdDtimes(new Timestamp(System.currentTimeMillis()));	
		globalParam.setVal("Y");
		
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.packet.maximum.count.offline.frequency", "10");
		applicationMap.put("mosip.registration.distance.from.machine.to.center", "100");
		applicationMap.put("mosip.registration.geo.capture.frequency", "Y");
		applicationMap.put("mosip.registration.masterSyncJob.frequency", "1");
		applicationMap.put("mosip.registration.last_export_registration_config_time", "1");
		applicationMap.put("mosip.registration.reg_pak_max_cnt_apprv_limit", "5");
		applicationMap.put("mosip.registration.reg_pak_max_time_apprv_limit", "5");
		applicationMap.put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "Y");
		applicationMap.put(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE, "N");
		applicationMap.put(RegistrationConstants.SOFTWARE_UPDATE_MAX_CONFIGURED_FREQ, "5");
		applicationMap.put("lastCapturedTime", Instant.now());
		when(context.map()).thenReturn(applicationMap);

		Mockito.when(globalParamDAO.get(globalParamId)).thenReturn(globalParam);

		Mockito.when(jobConfigDAO.getAll()).thenReturn(listSyncJob);
		Mockito.when(syncJobDAO.getRegistrationDetails()).thenReturn(registrationList);
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobInfo);
		Mockito.when(syncJobInfo.getSyncControlList()).thenReturn(null);
		Mockito.when(syncJobInfo.getYetToExportCount()).thenReturn((double) 20);

		Mockito.when(gpsFacade.getLatLongDtls(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString()))
				.thenReturn(map);

		ResponseDTO responseDTO = syncStatusValidatorServiceImpl.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs = responseDTO.getErrorResponseDTOs();

		assertEquals("REG-ICS‌-003", errorResponseDTOs.get(0).getCode());
		assertEquals("OPT_TO_REG_REACH_MAX_LIMIT", errorResponseDTOs.get(0).getMessage());
	}

}
