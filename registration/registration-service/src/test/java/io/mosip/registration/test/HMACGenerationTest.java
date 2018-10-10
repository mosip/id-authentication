package io.mosip.registration.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import io.mosip.registration.test.util.datastub.DataProvider;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.json.metadata.HashSequence;
import io.mosip.registration.util.hmac.HMACGeneration;

public class HMACGenerationTest {

	@Test
	public void generatePacketDTOTest() throws IOException, URISyntaxException {
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		byte[] demographicJsonBytes = "demographicJsonBytes".getBytes();
		byte[] hashArray = HMACGeneration.generatePacketDTOHash(registrationDTO, demographicJsonBytes,
				new HashSequence(new LinkedList<String>(), new LinkedList<String>(), new LinkedList<String>()));
		Assert.assertNotNull(hashArray);
	}

}
