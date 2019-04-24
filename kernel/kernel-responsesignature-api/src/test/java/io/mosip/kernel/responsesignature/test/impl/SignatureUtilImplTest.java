package io.mosip.kernel.responsesignature.test.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.responsesignature.util.SigningUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SignatureUtilImplTest {

	@MockBean
	SigningUtil signingUtil;
	
	@Autowired
	SignatureUtil signatureUtil;
	
	@Test
	public void testSigningUtil() {
		when(signingUtil.signResponseData(Mockito.anyString())).thenReturn("sdfds34sdfsdf34dddssfdds");
		signatureUtil.signResponse("MOSIP");
	}
}
