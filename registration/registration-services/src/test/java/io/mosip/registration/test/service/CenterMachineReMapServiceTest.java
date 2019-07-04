package io.mosip.registration.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.remap.impl.CenterMachineReMapServiceImpl;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, FileUtils.class, ScriptUtils.class })
public class CenterMachineReMapServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private CenterMachineReMapServiceImpl centerMachineReMapServiceImpl;
	@Mock
	private PacketSynchService packetSynchService;
	@Mock
	private PacketUploadService packetUploadService;
	@Mock
	private RegPacketStatusService regPacketStatusService;
	@Mock
	private RegistrationDAO registrationDAO;
	@Mock
	private SyncJobConfigDAO syncJobConfigDAO;
	@Mock
	private GlobalParamDAO globalParamDAO;
	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private DataSource dataSource;
	@Mock
	private Resource resource;

	@Mock
	private Connection connection;
	@Mock
	private AuditManagerService auditFactory;
	@Mock
	private PreRegistrationDataSyncDAO preRegistrationDataSyncDAO;
	@Autowired
	FileUtils fileUtils;
	@Mock
	GlobalParamService globalParamService;

	@BeforeClass
	public static void initialize() throws IOException, java.io.IOException {
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.registration_pre_reg_packet_location", "..//PreRegPacketStore");
		ApplicationContext.getInstance().setApplicationMap(applicationMap);

	}

	@Test
	public void handleRemapProcessTest() throws Exception {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.mockStatic(ScriptUtils.class);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.anyObject())).thenReturn(globalParam);
		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registrationList.add(registration);
		Mockito.when(registrationDAO.findByServerStatusCodeNotIn(Mockito.anyList())).thenReturn(registrationList);
		SyncJobDef syncJobDef = new SyncJobDef();
		List<SyncJobDef> syncJobDefList = new ArrayList<>();
		Mockito.when(syncJobConfigDAO.getActiveJobs()).thenReturn(syncJobDefList);
		Mockito.when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		List<PreRegistrationList> preRegistrationList = new ArrayList<>();
		Mockito.when(preRegistrationDataSyncDAO.getAllPreRegPackets()).thenReturn(preRegistrationList);

		PowerMockito.doNothing().when(FileUtils.class, "deleteDirectory", Mockito.any(File.class));

		for (int i = 1; i < 5; i++) {
			centerMachineReMapServiceImpl.handleReMapProcess(i);
		}

	}

	@Test
	public void HandleRemapTest() throws Exception {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.anyObject())).thenReturn(globalParam);
		List<Registration> registrationList = new ArrayList<>();

		Mockito.when(registrationDAO.findByServerStatusCodeNotIn(Mockito.anyList())).thenReturn(registrationList);
		SyncJobDef syncJobDef = new SyncJobDef();
		List<SyncJobDef> syncJobDefList = new ArrayList<>();
		syncJobDefList.add(syncJobDef);
		Mockito.when(syncJobConfigDAO.getActiveJobs()).thenReturn(syncJobDefList);
		Mockito.when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		List<PreRegistrationList> list = new ArrayList<>();
		PreRegistrationList preRegistrationList = new PreRegistrationList();
		list.add(preRegistrationList);
		Mockito.when(preRegistrationDataSyncDAO.getAllPreRegPackets()).thenReturn(list);
		List<Registration> regList = new ArrayList<>();
		Mockito.when(registrationDAO.getEnrollmentByStatus(Mockito.anyString())).thenReturn(regList);

		PowerMockito.doNothing().when(FileUtils.class, "deleteDirectory", Mockito.any(File.class));

		Mockito.doNothing().when(globalParamService).update("mosip.registration.initial_setup", "Y");

		for (int i = 1; i < 5; i++) {
			centerMachineReMapServiceImpl.handleReMapProcess(i);
		}
	}

	@Test
	public void PacketsPendingForEODTest() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);
		List<Registration> regList = new ArrayList<>();
		Mockito.when(registrationDAO.getEnrollmentByStatus(Mockito.anyString())).thenReturn(regList);
		assertNotNull(centerMachineReMapServiceImpl.isPacketsPendingForEOD());

	}

	@Test
	public void handleRemapProcessTestFailure() throws Exception {
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.anyObject())).thenReturn(globalParam);
		PowerMockito.doThrow(new IOException("error", "error")).when(FileUtils.class, "deleteDirectory",
				Mockito.any(File.class));
		centerMachineReMapServiceImpl.handleReMapProcess(3);

	}
	@Test
	public void startRemapProcessTest()
	{
		centerMachineReMapServiceImpl.startRemapProcess();
	}

}
