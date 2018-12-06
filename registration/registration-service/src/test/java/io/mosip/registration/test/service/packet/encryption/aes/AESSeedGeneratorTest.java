package io.mosip.registration.test.service.packet.encryption.aes;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.AESSeedGeneratorImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AESSeedGeneratorTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AESSeedGeneratorImpl aesSeedGeneratorImpl;

	@AfterClass
	public static void destroy() {
		SessionContext.destroySession();
	}
	
	@Test
	public void testGenerateAESKeySeeds() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setName("Operator Name");
		List<String> aesKeySeeds = aesSeedGeneratorImpl.generateAESKeySeeds();
		assertNotNull(aesKeySeeds);
		assertFalse(aesKeySeeds.isEmpty());
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testUncheckedException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(SessionContext.class, "userContext", null);
		aesSeedGeneratorImpl.generateAESKeySeeds();
	} 


}
