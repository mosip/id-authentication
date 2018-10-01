package org.mosip.registration.test;

import java.util.List;

import org.junit.Test;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import org.mosip.registration.test.config.SpringConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

public class AESSeedGeneratorTest extends SpringConfiguration{

	@Autowired
	private AESSeedGenerator aesSeedGenerator;

	@Test
	public void testGenerateAESKeySeeds() throws RegBaseCheckedException {
		List<String> aesKeySeeds = aesSeedGenerator.generateAESKeySeeds();
		assertNotNull(aesKeySeeds);
		assertFalse(aesKeySeeds.isEmpty());
	}

}
