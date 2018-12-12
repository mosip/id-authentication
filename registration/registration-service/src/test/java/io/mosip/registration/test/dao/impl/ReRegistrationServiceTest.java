package io.mosip.registration.test.dao.impl;


import static org.junit.Assert.assertEquals;

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

import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.impl.ReRegistrationServiceImpl;

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
		List<Registration> reRegisterList = new ArrayList<>();
		Registration reg = new Registration();
		reg.setId("11111");
		reg.setAckFilename("path");
		reRegisterList.add(reg);
		Mockito.when(registrationDAO.getAllReRegistrationPackets(packetStatus)).thenReturn(reRegisterList);
		assertEquals("11111", reRegistrationServiceImpl.getAllReRegistrationPackets().get(0).getFileName());
	}
	
	@Test
	public void testUpdateReRegistrationStatus() {
		Map<String, String> reRegistrationStatus=new HashMap<>();
		reRegistrationStatus.put("11111", "confirmed");
		Registration reg=new Registration();
		Mockito.when(registrationDAO.updateRegStatus(reg)).thenReturn(reg);
		assertEquals(true, reRegistrationServiceImpl.updateReRegistrationStatus(reRegistrationStatus));
	}
}
