package io.mosip.registration.test.service.packet.encryption;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.dao.RegTransactionDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.service.PacketUploadService;

public class PacketUploadServiceTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private RegistrationDAO registrationDAO;
	
	@Mock
	private RegTransactionDAO regTransactionDAO;
	
	@InjectMocks
	private PacketUploadService packetUploadService;

	@Mock
	private MosipLogger logger;

	@Test
	public void testVerifyPacket() {
		ReflectionTestUtils.setField(packetUploadService, "LOGGER", logger);
		List<Registration> registrations = new ArrayList<>();
		Registration registration = new Registration();
		registration.setClientStatusCode("P");
		registrations.add(registration);
		registration = new Registration();
		registration.setClientStatusCode("H");
		registrations.add(registration);registration = new Registration();
		registration.setClientStatusCode("H");
		registrations.add(registration);
		
		Mockito.when(registrationDAO.getRegistrationById(Mockito.anyListOf(String.class))).thenReturn(registrations);
		List<String> packetNames = new ArrayList<>();
		packetNames.add("2018782130000126092018200339");
		Map<String, File> packetMap = new HashMap<>();
		File packet=new File("");
		packetMap.put(packetNames.get(0), packet);
		List<File> files =  packetUploadService.verifyPacket(packetNames, packetMap);
		Assert.assertEquals(2, files.size());
	}
	

	@Test
	public void testUpdateStatus() {
		ReflectionTestUtils.setField(packetUploadService, "LOGGER", logger);
		List<File> uploadedPackets = new ArrayList<>();
		uploadedPackets.add(new File(""));
		Registration registration = new Registration();
		Mockito.when(registrationDAO.updateRegStatus(Mockito.anyString())).thenReturn(registration);
		RegistrationTransaction regTransaction = new RegistrationTransaction();
		List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
		registrationTransactions.add(regTransaction);
		//Mockito.when(regTransactionRepository.saveAll(registrationTransactions);
		Boolean status = packetUploadService.updateStatus(uploadedPackets);
		Assert.assertEquals(true, status);
	}

}
