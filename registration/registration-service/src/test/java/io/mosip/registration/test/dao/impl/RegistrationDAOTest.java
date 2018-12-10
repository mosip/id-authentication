package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.impl.RegistrationDAOImpl;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
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
	@InjectMocks
	private RegistrationDAOImpl registrationDAOImpl;
	@Mock
	private RegistrationRepository registrationRepository;
	@Mock
	private RegTransactionRepository regTransactionRepository;
	private RegistrationTransaction regTransaction;
	
	@BeforeClass
	public static void setUp() {
		SessionContext.destroySession();
	}
	
	@Before
	public void initialize() throws InstantiationException, IllegalAccessException {
		
		Timestamp time = new Timestamp(System.currentTimeMillis());
		regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId("11111");
		regTransaction.setTrnTypeCode(RegistrationClientStatusCode.CREATED.getCode());
		regTransaction.setStatusCode(RegistrationClientStatusCode.CREATED.getCode());
		regTransaction.setCrBy("Officer");
		regTransaction.setCrDtime(time);
		
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().getUserContext().setName("mosip");
		RegistrationCenterDetailDTO center = new RegistrationCenterDetailDTO();
		center.setRegistrationCenterId("abc123");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(center);
		List<String> roles=new ArrayList<>();
		roles.add("SUPERADMIN");
		roles.add("SUPERVISOR");
		SessionContext.getInstance().getUserContext().setRoles(roles);
	}

	@Test
	public void testSaveRegistration() throws RegBaseCheckedException {
		when(registrationRepository.create(Mockito.any(Registration.class))).thenReturn(new Registration());
		registrationDAOImpl.save("../PacketStore/28-Sep-2018/111111", "Applicant");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testTransactionException() throws RegBaseCheckedException {
		when(registrationRepository.create(Mockito.any(Registration.class))).thenThrow(RegBaseUncheckedException.class);
		registrationDAOImpl.save("file", "Invalid");
	}
	@Test
	public void getRegistrationByStatusTest() {
		
		List<Registration> packetLists = new ArrayList<>();
		Registration reg=new Registration();
		packetLists.add(reg);
		List<String> packetNames=Arrays.asList("P","resend","E");
		Mockito.when(registrationRepository.findByStatusCodes("P","resend","E")).thenReturn(packetLists);
		assertEquals(packetLists,registrationDAOImpl.getRegistrationByStatus(packetNames));  
	}
	
	@Test
	public void updateRegStatusTest() {
		Registration updatedPacket=new Registration();
		updatedPacket.setClientStatusCode("P");
		List<RegistrationTransaction> registrationTransactions=new ArrayList<>();
		registrationTransactions.add(new RegistrationTransaction());
		updatedPacket.setRegistrationTransaction(registrationTransactions);
		Mockito.when(registrationRepository.getOne(Mockito.anyString())).thenReturn(updatedPacket);
		Mockito.when(registrationRepository.update(updatedPacket)).thenReturn(updatedPacket);
		assertEquals(updatedPacket, registrationDAOImpl.updateRegStatus(updatedPacket));
	}
	
	@Test
	public void testUpdateStatusRegistration() throws RegBaseCheckedException {
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		Registration regobjectrequest = new Registration();
		regobjectrequest.setId("123456");
		regobjectrequest.setClientStatusCode("R");
		regobjectrequest.setIndividualName("Balaji S");
		regobjectrequest.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
		regobjectrequest.setApproverRoleCode(SessionContext.getInstance().getUserContext().getRoles().get(0));
		regobjectrequest.setAckFilename("file1");
		regobjectrequest.setRegistrationTransaction(new ArrayList<>());		
			
		when(registrationRepository.getOne(Mockito.anyString())).thenReturn(regobjectrequest);
		Registration regobj1=registrationRepository.getOne("123456");
		assertEquals("123456",regobj1.getId());
		assertEquals("mosip",regobj1.getUpdBy());
		assertEquals("R",regobj1.getClientStatusCode());
		assertEquals("SUPERADMIN",regobj1.getApproverRoleCode());
		assertEquals("Balaji S",regobj1.getIndividualName());
		assertEquals("file1",regobj1.getAckFilename());

		
		Registration registration=new Registration();
		registration.setClientStatusCode("A");
		registration.setApproverUsrId("Mosip1214");
		registration.setStatusComment("");
		registration.setUpdBy("Mosip1214");
		
		List<RegistrationTransaction> registrationTransaction = new ArrayList<>();
		RegistrationTransaction registrationTxn = new RegistrationTransaction();
		registrationTxn.setTrnTypeCode(RegistrationTransactionType.UPDATED.getCode());
		registrationTxn.setLangCode("ENG");
		registrationTxn.setStatusCode(RegistrationClientStatusCode.APPROVED.getCode());
		registrationTxn.setStatusComment("");
		registrationTxn.setCrBy("Mosip1214");
		registrationTxn.setCrDtime(timestamp);
		registrationTransaction.add(registrationTxn);
		registration.getRegistrationTransaction();

		when(registrationRepository.update(regobj1)).thenReturn(registration);
		Registration regobj=registrationDAOImpl.updateRegistration("123456", "", "A");
		assertEquals("Mosip1214",regobj.getUpdBy() );
		assertEquals("A",regobj.getClientStatusCode());
		assertEquals("Mosip1214",regobj.getApproverUsrId());
		assertEquals("",regobj.getStatusComment());
	}

	
	@Test
	public void testGetRegistrationsByStatus() {
		
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
	
	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {
		when(registrationRepository.update(Mockito.anyObject())).thenThrow(RegBaseUncheckedException.class);
		registrationDAOImpl.updateRegistration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
}
