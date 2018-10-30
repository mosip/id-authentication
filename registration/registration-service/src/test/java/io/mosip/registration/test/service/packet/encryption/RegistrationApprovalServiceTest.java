package io.mosip.registration.test.service.packet.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.RegistrationApprovalServiceImpl;

public class RegistrationApprovalServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private RegistrationApprovalServiceImpl registrationApprovalServiceImpl;
	@Mock
	private RegistrationApprovalUiDto registrationApprovalUiDto;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Mock
	RegistrationDAO registrationDAO;

	@Before
	public void initialize() throws IOException, URISyntaxException, RegBaseCheckedException {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);


		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(registrationApprovalServiceImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(registrationApprovalServiceImpl, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testGetAllEnrollments() {
		List<Registration> details = new ArrayList<>();
		Registration regobject = new Registration();
		RegistrationUserDetail regUserDetail=new RegistrationUserDetail();

		regUserDetail.setId("Mosip123");
		regUserDetail.setName("RegistrationOfficer");
		
		regobject.setId("123456");
		regobject.setClientStatusCode("R");
		regobject.setIndividualName("Balaji S");
		regobject.setCrBy("Mosip123");
		regobject.setAckFilename("file1");
		
		regobject.setUserdetail(regUserDetail);
		details.add(regobject);
		
		Mockito.when(registrationDAO.approvalList()).thenReturn(details);
		
		ReflectionTestUtils.setField(registrationApprovalServiceImpl, "registrationDAO", registrationDAO); 
				
		List<RegistrationApprovalUiDto> allEnrollments = registrationApprovalServiceImpl.getAllEnrollments();
		assertTrue(allEnrollments.size() > 0);
		assertEquals("123456",allEnrollments.get(0).getId());
		assertEquals("R",allEnrollments.get(0).getType() );
		assertEquals("Balaji S",allEnrollments.get(0).getName());
		assertEquals("Mosip123",allEnrollments.get(0).getOperatorId());
		assertEquals("RegistrationOfficer",allEnrollments.get(0).getOperatorName());
		assertEquals("file1",allEnrollments.get(0).getAcknowledgementFormPath());
		
	}

	@Test
	public void testGetEnrollmentByStatus() {
		List<Registration> details = new ArrayList<>();
		Registration regobject = new Registration();
		RegistrationUserDetail regUserDetail=new RegistrationUserDetail();

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
		
		List<Registration> enrollmentsByStatus = registrationApprovalServiceImpl.getEnrollmentByStatus("R");
		assertTrue(enrollmentsByStatus.size() > 0);
		assertEquals("123456",enrollmentsByStatus.get(0).getId());
		assertEquals("R",enrollmentsByStatus.get(0).getClientStatusCode() );
		assertEquals("Balaji S",enrollmentsByStatus.get(0).getIndividualName());
		assertEquals("Mosip123",enrollmentsByStatus.get(0).getCrBy());
		assertEquals("RegistrationOfficer",enrollmentsByStatus.get(0).getUserdetail().getName());
		assertEquals("file1",enrollmentsByStatus.get(0).getAckFilename());
		
	}

	@Test
	public void testPacketUpdateStatus() {
		Registration regobject = new Registration();
		RegistrationUserDetail regUserDetail=new RegistrationUserDetail();

		regUserDetail.setId("Mosip1214");
		regUserDetail.setName("RegistrationOfficerName");
		
		regobject.setId("123456");
		regobject.setClientStatusCode("A");
		regobject.setIndividualName("Balaji S");
		regobject.setCrBy("Mosip123");
		regobject.setUpdBy("Mosip1214");
		regobject.setAckFilename("file1");
		
		regobject.setUserdetail(regUserDetail);
		
		Mockito.when(registrationDAO.updateStatus("123456", "R", "Mosip1214", "", "Mosip1214")).thenReturn(regobject);
		
		ReflectionTestUtils.setField(registrationApprovalServiceImpl, "registrationDAO", registrationDAO); 
		
		Boolean updateStatus = registrationApprovalServiceImpl.packetUpdateStatus("123456", "R", "Mosip1214", "", "Mosip1214");
		assertTrue(updateStatus);
	}
	
	@Test
	public void testPacketUpdateStatusFailure() {
		
		Mockito.when(registrationDAO.updateStatus("", "", "", "", "")).thenReturn(null);
		
		ReflectionTestUtils.setField(registrationApprovalServiceImpl, "registrationDAO", registrationDAO); 
		
		Boolean updateStatus = registrationApprovalServiceImpl.packetUpdateStatus("", "", "", "", "");
		assertTrue(!updateStatus);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseUncheckedException {
		when(registrationDAO.approvalList()).thenThrow(RegBaseUncheckedException.class);
		registrationApprovalServiceImpl.getAllEnrollments();
	}


}
