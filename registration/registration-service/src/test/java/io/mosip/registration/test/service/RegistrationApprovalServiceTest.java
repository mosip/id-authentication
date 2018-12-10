package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.RegistrationApprovalServiceImpl;

public class RegistrationApprovalServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private RegistrationApprovalServiceImpl registrationApprovalServiceImpl;
	@Mock
	private RegistrationApprovalDTO registrationApprovalDTO;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Mock
	RegistrationDAO registrationDAO;

	@BeforeClass
	public static void setup() {
		SessionContext.destroySession();
	}
	
	@Before
	public void initialize() throws IOException, URISyntaxException, RegBaseCheckedException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		List<String> roles = new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setUserId("mosip1214");
		SessionContext.getInstance().getUserContext().setRoles(roles);
	}

	@Test
	public void testGetEnrollmentByStatus() {
		List<Registration> details = new ArrayList<>();
		Registration regobject = new Registration();
		RegistrationUserDetail regUserDetail = new RegistrationUserDetail();

		regUserDetail.setId("Mosip123");
		regUserDetail.setName("RegistrationOfficer");

		regobject.setId("123456");
		regobject.setClientStatusCode("R");
		regobject.setIndividualName("Balaji S");
		regobject.setCrBy("Mosip123");
		regobject.setAckFilename("file1");

		regobject.setUserdetail(regUserDetail);
		details.add(regobject);

		Mockito.when(registrationDAO.getEnrollmentByStatus("R")).thenReturn(details);

		ReflectionTestUtils.setField(registrationApprovalServiceImpl, "registrationDAO", registrationDAO);

		List<RegistrationApprovalDTO> enrollmentsByStatus = registrationApprovalServiceImpl
				.getEnrollmentByStatus("R");
		assertTrue(enrollmentsByStatus.size() > 0);
		assertEquals("123456", enrollmentsByStatus.get(0).getId());
		assertEquals("file1", enrollmentsByStatus.get(0).getAcknowledgementFormPath());

	}

	@Test
	public void testPacketUpdateStatus() {
		Registration regobject = new Registration();
		RegistrationUserDetail regUserDetail = new RegistrationUserDetail();

		regUserDetail.setId("Mosip1214");
		regUserDetail.setName("RegistrationOfficerName");

		regobject.setId("123456");
		regobject.setClientStatusCode("A");
		regobject.setIndividualName("Balaji S");
		regobject.setCrBy("Mosip123");
		regobject.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
		regobject.setApproverRoleCode(SessionContext.getInstance().getUserContext().getRoles().get(0));
		regobject.setAckFilename("file1");

		regobject.setUserdetail(regUserDetail);

		Mockito.when(registrationDAO.updateRegistration("123456", "", "R")).thenReturn(regobject);

		ReflectionTestUtils.setField(registrationApprovalServiceImpl, "registrationDAO", registrationDAO);

		Registration updateStatus = registrationApprovalServiceImpl.updateRegistration("123456", "", "R");

		assertTrue(updateStatus.getId().equals("123456"));
		assertTrue(updateStatus.getClientStatusCode().equals("A"));
		assertTrue(updateStatus.getUpdBy().equals("mosip1214"));
		assertTrue(updateStatus.getApproverRoleCode().equals("SUPERADMIN"));
		assertTrue(updateStatus.getAckFilename().equals("file1"));

	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {
		when( registrationDAO.getEnrollmentByStatus(Mockito.anyString())).thenThrow(RegBaseUncheckedException.class);
		registrationApprovalServiceImpl.getEnrollmentByStatus("ON_HOLD");
	}
}
