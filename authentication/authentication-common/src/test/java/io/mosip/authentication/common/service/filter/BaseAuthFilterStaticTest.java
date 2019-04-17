package io.mosip.authentication.common.service.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.authentication.common.service.filter.BaseAuthFilter;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.util.CryptoUtil;


@RunWith(PowerMockRunner.class)
@PrepareForTest(CryptoUtil.class)
public class BaseAuthFilterStaticTest {

	@Test(expected=IdAuthenticationAppException.class)
	public void encodetest() throws IdAuthenticationAppException {
		PowerMockito.mockStatic(CryptoUtil.class);
		String stringToDecode = "assad";
		Mockito.when(CryptoUtil.encodeBase64String(stringToDecode.getBytes())).thenThrow(new IllegalArgumentException());
		BaseAuthFilter.encode(stringToDecode);
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void decodetest() throws IdAuthenticationAppException {
		PowerMockito.mockStatic(CryptoUtil.class);
		String stringToDecode = "assad";
		Mockito.when(CryptoUtil.decodeBase64(stringToDecode)).thenThrow(new IllegalArgumentException());
		BaseAuthFilter.decode(stringToDecode);
	}
}

