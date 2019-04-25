package io.mosip.kernel.responsesignature.test.impl;

import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class SignatureUtilImplTest {

	@Autowired
	SignatureUtil signatureUtil;

	@Test
	public void testSigningUtil() {
		when(signatureUtil.signResponse(Mockito.anyString())).thenReturn("sdfds34sdfsdf34dddssfdds");
		signatureUtil.signResponse("MOSIP");
	}
}
