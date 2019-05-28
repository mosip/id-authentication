package io.mosip.registration.test.dao.impl;


import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.packet.impl.ReRegistrationServiceImpl;

public class ReRegistrationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	RegistrationDAO registrationDAO;

	@InjectMocks
	ReRegistrationServiceImpl reRegistrationServiceImpl;

	@Test
	public void testGetAllReRegistrationPackets() {
		String[] packetStatus = { RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode(),
				RegistrationConstants.RE_REGISTRATION_STATUS };
		Timestamp time = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());

		List<Registration> reRegisterList = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("11111");
		reg.setAckFilename("path");
		reg.setCrDtime(time);
		reRegisterList.add(reg);
		Mockito.when(registrationDAO.getAllReRegistrationPackets(packetStatus)).thenReturn(reRegisterList);
		assertEquals("11111", reRegistrationServiceImpl.getAllReRegistrationPackets().get(0).getFileName());
	}
	
	@Test
	public void testUpdateReRegistrationStatus() {
		Map<String, String> reRegistrationStatus=new HashMap<>();
		reRegistrationStatus.put("11111", "confirmed");
		PacketStatusDTO reg=new PacketStatusDTO();
		Registration packet = new Registration();
		Mockito.when(registrationDAO.updateRegStatus(reg)).thenReturn(packet);
		assertEquals(true, reRegistrationServiceImpl.updateReRegistrationStatus(reRegistrationStatus));
	}
}
