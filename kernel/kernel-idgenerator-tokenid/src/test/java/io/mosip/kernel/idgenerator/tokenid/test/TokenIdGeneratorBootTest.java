package io.mosip.kernel.idgenerator.tokenid.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;

import io.mosip.kernel.idgenerator.tokenid.impl.TokenIdGeneratorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TokenIdGeneratorImpl.class)
@SpringBootTest
public class TokenIdGeneratorBootTest {

	

	@Test(expected=RuntimeException.class)
	public void idGeneratorTest() throws Exception {
		TokenIdGeneratorImpl tokenIdGen = PowerMockito.spy(new TokenIdGeneratorImpl());
		PowerMockito.doReturn("589443345568379323532358900712678528").when(tokenIdGen, "appendChecksum", Mockito.anyInt(), Mockito.anyString(), Mockito.anyString());
		PowerMockito.doReturn("589443345568379323532358900712678528").when(tokenIdGen, "generateRandomId", Mockito.anyInt());
		PowerMockito.doReturn(null).when(tokenIdGen,"generateTokenId");
		TokenIdGeneratorImpl tokenIdGeneratorImpl=new TokenIdGeneratorImpl();
		tokenIdGeneratorImpl.generateId();
	}
}
