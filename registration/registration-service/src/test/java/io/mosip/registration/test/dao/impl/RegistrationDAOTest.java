package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.constants.RegClientStatusCode;
import io.mosip.registration.constants.RegTranType;
import io.mosip.registration.dao.impl.RegistrationDAOImpl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegTransactionRepository;
import io.mosip.registration.repositories.RegistrationRepository;

public class RegistrationDAOTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@InjectMocks
	private RegistrationDAOImpl registrationDAOImpl;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private RegTransactionRepository regTransactionRepository;
	private RegistrationTransaction regTransaction;
	
	@Before
	public void initialize() throws InstantiationException, IllegalAccessException {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		
		OffsetDateTime time = OffsetDateTime.now();
		regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId("11111");
		regTransaction.setTrnTypeCode(RegClientStatusCode.CREATED.getCode());
		regTransaction.setStatusCode(RegClientStatusCode.CREATED.getCode());
		regTransaction.setCrBy("Officer");
		regTransaction.setCrDtime(time);
		
		ReflectionTestUtils.invokeMethod(registrationDAOImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		
	}

	@Test
	public void testSaveRegistration() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		when(registrationRepository.create(Mockito.any(Registration.class))).thenReturn(new Registration());
		registrationDAOImpl.save("D:/Packet Store/28-Sep-2018/111111", "Applicant");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testTransactionException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		when(registrationRepository.create(Mockito.any(Registration.class))).thenThrow(RegBaseUncheckedException.class);
		registrationDAOImpl.save("file", "Invalid");
	}
	
	@Test
	public void getRegistrationByIdTest() {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		
		List<Registration> packetLists = new ArrayList<>();
		packetLists.add(new Registration());
		when(registrationRepository.findByIdIn(Mockito.anyListOf(String.class))).thenReturn(packetLists);
		List<String> packetNames=new ArrayList<>();
		registrationDAOImpl.getRegistrationById(packetNames);
	}
	
	@Test
	public void updateRegStatusTest() {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		Registration updatedPacket=new Registration();
		Mockito.when(registrationRepository.getOne(Mockito.anyString())).thenReturn(updatedPacket);
		registrationDAOImpl.updateRegStatus("111111");
		assertEquals("P", updatedPacket.getClientStatusCode());
	}
	
	@Test
	public void testUpdateStatusRegistration() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		
		OffsetDateTime timestamp = OffsetDateTime.now();
		
		Registration regobjectrequest = new Registration();
		regobjectrequest.setId("123456");
		regobjectrequest.setClientStatusCode("R");
		regobjectrequest.setIndividualName("Balaji S");
		regobjectrequest.setCrBy("Mosip123");
		regobjectrequest.setAckFilename("file1");
		regobjectrequest.setRegistrationTransaction(new ArrayList<>());		
			
		when(registrationRepository.getOne(Mockito.anyString())).thenReturn(regobjectrequest);
		Registration regobj1=registrationRepository.getOne("123456");
		assertEquals("123456",regobj1.getId());
		assertEquals("Mosip123",regobj1.getCrBy());
		assertEquals("R",regobj1.getClientStatusCode());
		assertEquals("Balaji S",regobj1.getIndividualName());
		assertEquals("file1",regobj1.getAckFilename());

		
		Registration registration=new Registration();
		registration.setClientStatusCode("A");
		registration.setApproverUsrId("Mosip1214");
		registration.setStatusComment("");
		registration.setUpdBy("Mosip1214");
		
		List<RegistrationTransaction> registrationTransaction = new ArrayList<>();
		RegistrationTransaction registrationTxn = new RegistrationTransaction();
		registrationTxn.setTrnTypeCode(RegTranType.UPDATED.getCode());
		registrationTxn.setLangCode("ENG");
		registrationTxn.setIsActive(true);
		registrationTxn.setStatusCode(RegClientStatusCode.APPROVED.getCode());
		registrationTxn.setStatusComment("");
		registrationTxn.setCrBy("Mosip1214");
		registrationTxn.setCrDtime(timestamp);
		registrationTransaction.add(registrationTxn);
		registration.getRegistrationTransaction();

		when(registrationRepository.update(regobj1)).thenReturn(registration);
		Registration regobj=registrationDAOImpl.updateStatus("123456", "A", "Mosip1214", "", "Mosip1214");
		assertEquals("Mosip1214",regobj.getUpdBy() );
		assertEquals("A",regobj.getClientStatusCode());
		assertEquals("Mosip1214",regobj.getApproverUsrId());
		assertEquals("",regobj.getStatusComment());
	}

	@Test
	public void testApprovalListRegistration() {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		
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
				
		Mockito.when(registrationRepository.findByclientStatusCode("R")).thenReturn(details);
				
		List<Registration> enrollmentsByStatus = registrationDAOImpl.approvalList();
		assertTrue(enrollmentsByStatus.size() > 0);
		assertEquals("123456",enrollmentsByStatus.get(0).getId());
		assertEquals("R",enrollmentsByStatus.get(0).getClientStatusCode() );
		assertEquals("Balaji S",enrollmentsByStatus.get(0).getIndividualName());
		assertEquals("Mosip123",enrollmentsByStatus.get(0).getCrBy());
		assertEquals("RegistrationOfficer",enrollmentsByStatus.get(0).getUserdetail().getName());
		assertEquals("file1",enrollmentsByStatus.get(0).getAckFilename());
	}
	
	@Test
	public void testGetRegistrationsByStatus() {
		ReflectionTestUtils.setField(registrationDAOImpl, "logger", logger);
		
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
				
		Mockito.when(registrationRepository.findByclientStatusCode("R")).thenReturn(details);
				
		List<Registration> enrollmentsByStatus = registrationDAOImpl.getEnrollmentByStatus("R");
		assertTrue(enrollmentsByStatus.size() > 0);
		assertEquals("123456",enrollmentsByStatus.get(0).getId());
		assertEquals("R",enrollmentsByStatus.get(0).getClientStatusCode() );
		assertEquals("Balaji S",enrollmentsByStatus.get(0).getIndividualName());
		assertEquals("Mosip123",enrollmentsByStatus.get(0).getCrBy());
		assertEquals("RegistrationOfficer",enrollmentsByStatus.get(0).getUserdetail().getName());
		assertEquals("file1",enrollmentsByStatus.get(0).getAckFilename());
	}
}
