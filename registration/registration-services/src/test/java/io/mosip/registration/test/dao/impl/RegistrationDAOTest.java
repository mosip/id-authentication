package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.impl.RegistrationDAOImpl;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegTransactionRepository;
import io.mosip.registration.repositories.RegistrationRepository;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class })
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

	@Before
	public void initialize() throws Exception {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId("11111");
		regTransaction.setTrnTypeCode(RegistrationClientStatusCode.CREATED.getCode());
		regTransaction.setStatusCode(RegistrationClientStatusCode.CREATED.getCode());
		regTransaction.setCrBy("Officer");
		regTransaction.setCrDtime(time);

		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		List<String> roles = Arrays.asList("SUPERADMIN", "SUPERVISOR");
		RegistrationCenterDetailDTO center = new RegistrationCenterDetailDTO();
		center.setRegistrationCenterId("abc123");

		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");
		PowerMockito.when(SessionContext.userContext().getName()).thenReturn("mosip");
		PowerMockito.when(SessionContext.userContext().getRoles()).thenReturn(roles);
		PowerMockito.when(SessionContext.userContext().getRegistrationCenterDetailDTO()).thenReturn(center);
	}

	@Test
	public void testSaveRegistration() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		RegistrationMetaDataDTO registrationMetaDataDTO=new RegistrationMetaDataDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		IndividualIdentity identity = new IndividualIdentity();
		List<ValuesDTO> fullNames = new ArrayList<>();
		ValuesDTO valuesDTO = new ValuesDTO();
		valuesDTO.setLanguage("eng");
		valuesDTO.setValue("Individual Name");
		fullNames.add(valuesDTO);
		identity.setFullName(fullNames);
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);
		registrationDTO.setDemographicDTO(demographicDTO);
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);
		registrationDTO.getRegistrationMetaDataDTO().setRegistrationCategory("New");
		when(registrationRepository.create(Mockito.any(Registration.class))).thenReturn(new Registration());

		registrationDAOImpl.save("../PacketStore/28-Sep-2018/111111", registrationDTO);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testTransactionException() throws RegBaseCheckedException {
		when(registrationRepository.create(Mockito.any(Registration.class))).thenThrow(RegBaseUncheckedException.class);

		registrationDAOImpl.save("file", new RegistrationDTO());
	}

	@Test
	public void getRegistrationByStatusTest() {

		List<Registration> packetLists = new ArrayList<>();
		Registration reg = new Registration();
		packetLists.add(reg);
		List<String> packetNames = Arrays.asList("PUSHED", "EXPORTED", "resend", "E");
		Mockito.when(registrationRepository.findByStatusCodes("PUSHED", "EXPORTED", "resend", "E"))
				.thenReturn(packetLists);
		assertEquals(packetLists, registrationDAOImpl.getRegistrationByStatus(packetNames));
	}

	@Test
	public void updateRegStatusTest() {
		Registration updatedPacket = new Registration();
		updatedPacket.setUploadCount((short)0);
		List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
		registrationTransactions.add(new RegistrationTransaction());
		updatedPacket.setRegistrationTransaction(registrationTransactions);
		Mockito.when(registrationRepository.getOne(Mockito.anyString())).thenReturn(updatedPacket);
		Mockito.when(registrationRepository.update(updatedPacket)).thenReturn(updatedPacket);
		
		PacketStatusDTO packetStatusDTO=new PacketStatusDTO();
		packetStatusDTO.setPacketClientStatus("P");
		assertEquals(updatedPacket, registrationDAOImpl.updateRegStatus(packetStatusDTO));
	}

	@Test
	public void testUpdateStatusRegistration() throws RegBaseCheckedException {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		Registration regobjectrequest = new Registration();
		regobjectrequest.setId("123456");
		regobjectrequest.setClientStatusCode("R");
		regobjectrequest.setUpdBy("mosip");
		regobjectrequest.setApproverRoleCode("SUPERADMIN");
		regobjectrequest.setAckFilename("file1");
		regobjectrequest.setRegistrationTransaction(new ArrayList<>());

		when(registrationRepository.getOne(Mockito.anyString())).thenReturn(regobjectrequest);
		Registration regobj1 = registrationRepository.getOne("123456");
		assertEquals("123456", regobj1.getId());
		assertEquals("mosip", regobj1.getUpdBy());
		assertEquals("R", regobj1.getClientStatusCode());
		assertEquals("SUPERADMIN", regobj1.getApproverRoleCode());
		assertEquals("file1", regobj1.getAckFilename());

		Registration registration = new Registration();
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
		Registration regobj = registrationDAOImpl.updateRegistration("123456", "", "A");
		assertEquals("Mosip1214", regobj.getUpdBy());
		assertEquals("A", regobj.getClientStatusCode());
		assertEquals("Mosip1214", regobj.getApproverUsrId());
		assertEquals("", regobj.getStatusComment());
	}

	@Test
	public void testGetRegistrationsByStatus() {

		List<Registration> details = new ArrayList<>();
		Registration regobject = new Registration();
		UserDetail regUserDetail = new UserDetail();

		regUserDetail.setId("Mosip123");
		regUserDetail.setName("RegistrationOfficer");

		regobject.setId("123456");
		regobject.setClientStatusCode("R");
		regobject.setCrBy("Mosip123");
		regobject.setAckFilename("file1");

		regobject.setUserdetail(regUserDetail);
		details.add(regobject);

		Mockito.when(registrationRepository.findByclientStatusCodeOrderByCrDtime("R")).thenReturn(details);

		List<Registration> enrollmentsByStatus = registrationDAOImpl.getEnrollmentByStatus("R");
		assertTrue(enrollmentsByStatus.size() > 0);
		assertEquals("123456", enrollmentsByStatus.get(0).getId());
		assertEquals("R", enrollmentsByStatus.get(0).getClientStatusCode());
		assertEquals("Mosip123", enrollmentsByStatus.get(0).getCrBy());
		assertEquals("RegistrationOfficer", enrollmentsByStatus.get(0).getUserdetail().getName());
		assertEquals("file1", enrollmentsByStatus.get(0).getAckFilename());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testValidateException() throws RegBaseCheckedException {
		when(registrationRepository.update(Mockito.anyObject())).thenThrow(RegBaseUncheckedException.class);
		registrationDAOImpl.updateRegistration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void getAllReRegistrationPacketsTest() {
		List<Registration> registrations = new LinkedList<>();
		String[] status = { "Approved", "Rejected" };
		when(registrationRepository.findByClientStatusCodeAndServerStatusCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(registrations);
		assertEquals(registrationDAOImpl.getAllReRegistrationPackets(status), registrations);
	}

	@Test
	public void updatePacketSyncStatusTest() {
		Registration registration = new Registration();
		List<RegistrationTransaction> registrationTransaction = new LinkedList<>();
		registration.setRegistrationTransaction(registrationTransaction);

		when(registrationRepository.getOne(Mockito.anyString())).thenReturn(registration);
		when(registrationRepository.update(Mockito.any())).thenReturn(registration);

		//registrationDAOImpl.updatePacketSyncStatus(registration);
	}

	@Test
	public void getPacketsToBeSynchedTest() {
		List<Registration> registration = new LinkedList<>();
		List<String> statusCodes = new LinkedList<>();

		when(registrationRepository.findByClientStatusCodeInOrderByUpdDtimesDesc(statusCodes)).thenReturn(registration);

		registrationDAOImpl.getPacketsToBeSynched(statusCodes);
	}

	@Test
	public void testgetRegistrationById() {
		Registration registration = new Registration();
		registration.setId("123456789");
		registration.setClientStatusCode("APPROVED");
		registration.setCrBy("mosip");

		Mockito.when(registrationRepository.findByClientStatusCodeAndId("APPROVED", "123456789"))
				.thenReturn(registration);
		Registration reg = registrationDAOImpl.getRegistrationById("APPROVED", "123456789");
		assertEquals("123456789", reg.getId());
		assertEquals("APPROVED", reg.getClientStatusCode());
		assertEquals("mosip", reg.getCrBy());
	}

	@Test
	public void getRegistrationByIdTest() {
		Registration registration = new Registration();
		Mockito.when(registrationRepository.findByClientStatusCodeAndId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(registration);
		assertSame(registration, registrationDAOImpl.getRegistrationById("PROCESSED", "REG123456"));

	}

	@Test
	public void getRegistrationsTest() {
		List<String> ids = new LinkedList<>();
		ids.add("REG123456");
		List<Registration> registrations = new LinkedList<>();

		Registration registration = new Registration();
		registrations.add(registration);

		Mockito.when(registrationRepository.findAllById(ids)).thenReturn(registrations);
		assertSame(registrations, registrationDAOImpl.get(ids));

	}

}
