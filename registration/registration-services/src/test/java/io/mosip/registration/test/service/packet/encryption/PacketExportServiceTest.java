package io.mosip.registration.test.service.packet.encryption;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.packet.impl.PacketExportServiceImpl;

public class PacketExportServiceTest {
	
	@Rule
	public MockitoRule mockitoRule=MockitoJUnit.rule();
	
	@Mock
	private RegistrationDAO registrationDAO;
	
	@InjectMocks
	private PacketExportServiceImpl packetExportServiceImpl;
	
	@SuppressWarnings("unchecked")
	@Test
	public void getSynchedRecordsTest() {
		Registration reg = new Registration();
		List<Registration> packetList = new ArrayList<>();
		packetList.add(reg);
		Mockito.when(registrationDAO.getPacketsToBeSynched(Mockito.anyList())).thenReturn(packetList);
		assertEquals(1, packetExportServiceImpl.getSynchedRecords().size());
	}
	
	@Test
	public void updateRegistrationStatusTest() {
		Registration reg=new Registration();
		List<Registration> updatedExportPackets = new ArrayList<>();
		updatedExportPackets.add(reg);
		Mockito.when(registrationDAO.updateRegStatus(Mockito.anyObject())).thenReturn(reg);
		assertEquals("Success", packetExportServiceImpl.updateRegistrationStatus(updatedExportPackets).getSuccessResponseDTO().getMessage());
	}
	

}
