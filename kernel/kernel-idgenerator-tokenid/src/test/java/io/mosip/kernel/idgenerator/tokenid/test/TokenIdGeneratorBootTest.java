package io.mosip.kernel.idgenerator.tokenid.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.idgenerator.tokenid.impl.TokenIdGeneratorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TokenIdGeneratorImpl.class)
public class TokenIdGeneratorBootTest {

	@Autowired
	TokenIdGenerator<String> tokenIdGenerator;
	
	
	
	@Test
	public void idGeneratorTest() throws Exception {
		TokenIdGeneratorImpl tokenIdGen=PowerMockito.spy(new TokenIdGeneratorImpl());
		PowerMockito.when(tokenIdGen,"appendChecksum",Mockito.anyInt(),Mockito.anyString(),Mockito.anyString()).thenReturn(Mockito.anyString());
		PowerMockito.when(tokenIdGen,"generateRandomId",Mockito.anyInt()).thenReturn(Mockito.anyString());
		PowerMockito.when(tokenIdGen,"generateTokenId").thenReturn(null);
		tokenIdGenerator.generateId();
	}
}

