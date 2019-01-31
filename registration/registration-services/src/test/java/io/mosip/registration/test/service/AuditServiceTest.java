package io.mosip.registration.test.service;

import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.audit.impl.AuditServiceImpl;
import io.mosip.registration.service.packet.RegPacketStatusService;

public class AuditServiceTest {

	@Mock
	private RegistrationDAO registrationDAO;

	@Mock
	private RegPacketStatusService regPacketStatusService;

	@Mock
	private AuditLogControlDAO auditLogControlDAO;

	@Mock
	io.mosip.registration.context.ApplicationContext context;

	@Mock
	Map<String, Object> applicationMap;
	
	@InjectMocks
	private AuditServiceImpl auditServiceImpl;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Before
	public void intiate() {

		Mockito.when(context.getApplicationMap()).thenReturn(applicationMap);
		Mockito.when(applicationMap.get(Mockito.anyString())).thenReturn("45");

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
		
		
		Mockito.when(auditLogControlDAO.get(Mockito.any())).thenReturn(list);
		Mockito.when(registrationDAO.get(Mockito.anyList())).thenReturn(registrations);
		
		Mockito.doNothing().when(regPacketStatusService).deleteRegistrations(registrations);
		
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, auditServiceImpl.deleteAuditLogs().getSuccessResponseDTO().getMessage());
		list.clear();
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_EMPTY_MSG, auditServiceImpl.deleteAuditLogs().getSuccessResponseDTO().getMessage());
		
	}
	
	@Test
	public void auditLogsDeletionFailureTest() {
		Mockito.when(applicationMap.get(Mockito.anyString())).thenReturn(null);
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, auditServiceImpl.deleteAuditLogs().getSuccessResponseDTO().getMessage());
		
	}
	
	@Test
	public void auditLogsDeletionExceptionTest() {
		Mockito.when(auditLogControlDAO.get(Mockito.any())).thenThrow(RuntimeException.class);
		
		assertSame(RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, auditServiceImpl.deleteAuditLogs().getSuccessResponseDTO().getMessage());
		
	}

}
