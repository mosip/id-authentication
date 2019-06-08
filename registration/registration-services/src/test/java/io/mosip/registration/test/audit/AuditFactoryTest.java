package io.mosip.registration.test.audit;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
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

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.packet.RegPacketStatusService;

@RunWith(PowerMockRunner.class)
<<<<<<< HEAD
@PrepareForTest({ InetAddress.class, ApplicationContext.class, SessionContext.class })
=======
@PrepareForTest({ InetAddress.class, SessionContext.class, ApplicationContext.class })
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
public class AuditFactoryTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;
	@InjectMocks
	private AuditManagerSerivceImpl auditFactory;

	@Mock
	private RegistrationDAO registrationDAO;

	@Mock
	private RegPacketStatusService regPacketStatusService;

	@Mock
	private AuditLogControlDAO auditLogControlDAO;

	@Mock
	Map<String, Object> applicationMap;

	@Mock
	private GlobalParamService globalParamService;
	@Mock
	io.mosip.registration.context.ApplicationContext context;

<<<<<<< HEAD
	@Before
	public void initialize() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.DEFAULT_HOST_IP, "127.0.0.0");
		appMap.put(RegistrationConstants.DEFAULT_HOST_NAME, "LOCALHOST");
		appMap.put(RegistrationConstants.APP_NAME, "REGISTRATION");
		appMap.put(RegistrationConstants.APP_ID, "REG");

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
	}

=======
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	@Test
	public void auditTest() throws Exception {
		PowerMockito.mockStatic(InetAddress.class, SessionContext.class);
		PowerMockito.when(InetAddress.getLocalHost()).thenCallRealMethod();
		PowerMockito.doReturn("userId").when(SessionContext.class, "userId");
		PowerMockito.doReturn("userName").when(SessionContext.class, "userName");
		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);

		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "id", "ref");
	}

	@Test
	public void auditTestWithDefaultValues() throws Exception {
		PowerMockito.mockStatic(InetAddress.class);
		PowerMockito.when(InetAddress.getLocalHost()).thenThrow(new UnknownHostException("Unknown"));
		when(auditHandler.addAudit(Mockito.any(AuditRequestDto.class))).thenReturn(true);

		auditFactory.audit(AuditEvent.PACKET_APPROVED, Components.PACKET_CREATOR, "id", "ref");
	}
	
	

	@Before
	public void intiate() {
<<<<<<< HEAD
		PowerMockito.mockStatic(ApplicationContext.class);
		when(ApplicationContext.map()).thenReturn(applicationMap);
		Mockito.when(applicationMap.get(Mockito.anyString())).thenReturn("45");
		when(io.mosip.registration.context.ApplicationContext.getInstance()).thenReturn(context);
		Map<String, Object> map = new HashMap<>();
		map.put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS, "5");
		
		Mockito.when(globalParamService.getGlobalParams()).thenReturn(map);
		
		auditFactory.setBaseGlobalMap(applicationMap);
=======
		Map<String, Object> map = new HashMap<>();
		map.put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS, "5");
		map.put(RegistrationConstants.DEFAULT_HOST_IP, "127.0.0.0");
		map.put(RegistrationConstants.DEFAULT_HOST_NAME, "LOCALHOST");
		map.put(RegistrationConstants.APP_NAME, "REGISTRATION");
		map.put(RegistrationConstants.APP_ID, "REG");
		
		PowerMockito.mockStatic(ApplicationContext.class);
		when(ApplicationContext.map()).thenReturn(map);
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082

	}

	@Test
	public void deleteAuditLogsSuccessTest() {
		
	
		List<AuditLogControl> list = new LinkedList<>();
		AuditLogControl auditLogControl = new AuditLogControl();
		auditLogControl.setRegistrationId("REG123456");
		list.add(auditLogControl);
		
		List<Registration> registrations = new LinkedList<>();
		Registration registration = new Registration();
		registration.setId("REG123456");
		registrations.add(registration);
		
		
		Mockito.when(auditLogControlDAO.get(new Timestamp(Mockito.anyLong()))).thenReturn(list);
<<<<<<< HEAD
		Mockito.when(registrationDAO.get(Mockito.anyList())).thenReturn(registrations);
		
=======
		Mockito.when(registrationDAO.get(Mockito.anyListOf(String.class))).thenReturn(registrations);

>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
		Mockito.doNothing().when(regPacketStatusService).deleteRegistrations(registrations);
		
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, auditFactory.deleteAuditLogs().getSuccessResponseDTO().getMessage());
		list.clear();
		Mockito.when(auditLogControlDAO.get(new Timestamp(Mockito.anyLong()))).thenReturn(list);
		
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_EMPTY_MSG, auditFactory.deleteAuditLogs().getSuccessResponseDTO().getMessage());
		
	}
	
	
	@Test
	public void auditLogsDeletionFailureTest() {
		Mockito.when(applicationMap.get(Mockito.anyString())).thenReturn(null);
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, auditFactory.deleteAuditLogs().getErrorResponseDTOs().get(0).getMessage());
		
	}
<<<<<<< HEAD
	
=======

	@SuppressWarnings("unchecked")
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	@Test
	public void auditLogsDeletionExceptionTest() {
		Mockito.when(auditLogControlDAO.get(new Timestamp(Mockito.anyLong()))).thenThrow(RuntimeException.class);
		
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, auditFactory.deleteAuditLogs().getErrorResponseDTOs().get(0).getMessage());
		
	}

}
