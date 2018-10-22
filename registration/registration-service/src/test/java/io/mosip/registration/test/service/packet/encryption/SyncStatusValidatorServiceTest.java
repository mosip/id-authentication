package io.mosip.registration.test.service.packet.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobDAO.SyncJobInfo;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.GeoLocationCapture;
import io.mosip.registration.service.SyncStatusValidatorService;
import io.mosip.registration.test.config.SpringConfiguration;

public class SyncStatusValidatorServiceTest extends SpringConfiguration{

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private SyncStatusValidatorService syncStatusValidatorService ;
	@Mock
	private SyncJobDAO syncJobDAO;
	@Mock
	private Environment environment;
	@Mock
	private SyncJobInfo syncJobnfo;
	@Mock
	private GeoLocationCapture geoLocationCapture;
	@Mock
	private AuditFactory auditFactory;
	
	@BeforeClass
	public static void beforeClass() {
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
	}
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterLatitude("12.991276");
		centerDetailDTO.setRegistrationCenterLongitude("80.2461");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);

		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(syncStatusValidatorService, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(syncStatusValidatorService, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		doNothing().when(auditFactory).audit(Mockito.any(AuditEventEnum.class), Mockito.any(AppModuleEnum.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testValidateSyncStatusFailureCase() {
		SyncControl syncControl1=new SyncControl();
		syncControl1.setSJobId("MDS_J00001");
		syncControl1.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:05:19.199+05:30"));
		SyncControl syncControl2=new SyncControl();
		syncControl2.setSJobId("LER_J00009");
		syncControl2.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:25:13.199+05:30"));
		
		List<SyncControl> listSync=new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);
		
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put("errorMessage", "success");
		
		Mockito.when(geoLocationCapture.getLatLongDtls()).thenReturn(map);
		Mockito.when(environment.getProperty("GEO_CAP_FREQ")).thenReturn("Y");
		Mockito.when(environment.getProperty("MDS_J00001")).thenReturn("1");
		Mockito.when(environment.getProperty("LER_J00009")).thenReturn("1");
		Mockito.when(environment.getProperty("DIST_FRM_MACHN_TO_CENTER")).thenReturn("100");
		Mockito.when(environment.getProperty("REG_PAK_MAX_CNT_OFFLINE_FREQ")).thenReturn("10");
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobnfo);
		Mockito.when(syncJobnfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobnfo.getYetToExportCount()).thenReturn((double) 20);
		
		ResponseDTO responseDTO=syncStatusValidatorService.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs= responseDTO.getErrorResponseDTOs();
		
		assertEquals("REG-ICS‌-002",errorResponseDTOs.get(0).getCode());
		assertEquals("Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001",errorResponseDTOs.get(1).getCode());
		assertEquals("Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003",errorResponseDTOs.get(2).getCode());
		assertEquals("Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-004",errorResponseDTOs.get(3).getCode());
		assertEquals("Your client machine’s location is outside the registration centre. Please note that registration can be done only from within the registration centre",errorResponseDTOs.get(3).getMessage());
		
	}

	@Test
	public void testValidateSyncStatusSuccessCase() {
		SyncControl syncControl1=new SyncControl();
		syncControl1.setSJobId("MDS_J00001");
		syncControl1.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:05:19.199+05:30"));
		SyncControl syncControl2=new SyncControl();
		syncControl2.setSJobId("LER_J00009");
		syncControl2.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:25:13.199+05:30"));
		
		List<SyncControl> listSync=new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);

		Map<String, Object> map=new HashMap<String, Object>();
		map.put("latitude", 12.99194);
		map.put("longitude", 80.2471);
		map.put("errorMessage", "success");
		
		Mockito.when(geoLocationCapture.getLatLongDtls()).thenReturn(map);
		Mockito.when(environment.getProperty("GEO_CAP_FREQ")).thenReturn("N");
		Mockito.when(environment.getProperty("MDS_J00001")).thenReturn("20");
		Mockito.when(environment.getProperty("LER_J00009")).thenReturn("20");
		Mockito.when(environment.getProperty("DIST_FRM_MACHN_TO_CENTER")).thenReturn("215");
		Mockito.when(environment.getProperty("REG_PAK_MAX_CNT_OFFLINE_FREQ")).thenReturn("100");
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobnfo);
		Mockito.when(syncJobnfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobnfo.getYetToExportCount()).thenReturn((double) 20);
		
		ResponseDTO responseDTO=syncStatusValidatorService.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs= responseDTO.getErrorResponseDTOs();
		assertTrue(errorResponseDTOs.isEmpty());
		
	}
	
	@Test
	public void testValidateGpsSyncStatusFailureCase() {
		SyncControl syncControl1=new SyncControl();
		syncControl1.setSJobId("MDS_J00001");
		syncControl1.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:05:19.199+05:30"));
		SyncControl syncControl2=new SyncControl();
		syncControl2.setSJobId("LER_J00009");
		syncControl2.setLastSyncDtimez(OffsetDateTime.parse("2018-10-05T18:25:13.199+05:30"));
		
		List<SyncControl> listSync=new ArrayList<>();
		listSync.add(syncControl1);
		listSync.add(syncControl2);
		
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("latitude", 0.0);
		map.put("longitude", 0.0);
		map.put("errorMessage", "failure");
		
		Mockito.when(geoLocationCapture.getLatLongDtls()).thenReturn(map);
		Mockito.when(environment.getProperty("GEO_CAP_FREQ")).thenReturn("Y");
		Mockito.when(environment.getProperty("MDS_J00001")).thenReturn("1");
		Mockito.when(environment.getProperty("LER_J00009")).thenReturn("1");
		Mockito.when(environment.getProperty("DIST_FRM_MACHN_TO_CENTER")).thenReturn("100");
		Mockito.when(environment.getProperty("REG_PAK_MAX_CNT_OFFLINE_FREQ")).thenReturn("10");
		Mockito.when(syncJobDAO.getSyncStatus()).thenReturn(syncJobnfo);
		Mockito.when(syncJobnfo.getSyncControlList()).thenReturn(listSync);
		Mockito.when(syncJobnfo.getYetToExportCount()).thenReturn((double) 20);
		
		ResponseDTO responseDTO=syncStatusValidatorService.validateSyncStatus();
		List<ErrorResponseDTO> errorResponseDTOs= responseDTO.getErrorResponseDTOs();
		
		assertEquals("REG-ICS‌-002",errorResponseDTOs.get(0).getCode());
		assertEquals("Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration",errorResponseDTOs.get(0).getMessage());
		assertEquals("REG-ICS‌-001",errorResponseDTOs.get(1).getCode());
		assertEquals("Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration",errorResponseDTOs.get(1).getMessage());
		assertEquals("REG-ICS‌-003",errorResponseDTOs.get(2).getCode());
		assertEquals("Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration",errorResponseDTOs.get(2).getMessage());
		assertEquals("REG-ICS‌-005",errorResponseDTOs.get(3).getCode());
		assertEquals("Unable to validate machine location. Please insert the GPS device and try again",errorResponseDTOs.get(3).getMessage());
		
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {
		when(syncJobDAO.getSyncStatus()).thenThrow(RegBaseUncheckedException.class);
		syncStatusValidatorService.validateSyncStatus();
	}

}
