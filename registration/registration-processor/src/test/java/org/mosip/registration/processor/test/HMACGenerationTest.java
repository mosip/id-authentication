package org.mosip.registration.processor.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import org.mosip.registration.processor.test.util.datastub.DataProvider;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.dto.PacketDTO;
import org.mosip.registration.processor.util.hmac.HMACGeneration;

public class HMACGenerationTest {
	
	@Test
	public void generatePacketDTOTest() throws IOException, URISyntaxException {
		EnrollmentDTO enrollmentDTO=DataProvider.getEnrollmentDTO();
		PacketDTO packetDTO=enrollmentDTO.getPacketDTO();
		byte[] demographicJsonBytes="demographicJsonBytes".getBytes();
		byte[] hashArray=HMACGeneration.generatePacketDtoHash(packetDTO, demographicJsonBytes, new LinkedList<String>(),new LinkedList<String>(),new LinkedList<String>());
		Assert.assertNotNull(hashArray);
	}

}
