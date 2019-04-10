package io.mosip.authentication.common.authentication.filter;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.authentication.common.authentication.filter.BaseAuthFilter;
import io.mosip.authentication.common.authentication.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.util.CryptoUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CryptoUtil.class)
public class BaseAuthFilterStaticTest {

	BaseAuthFilter baseAuthFilter = new BaseAuthFilter() {

		@Override
		protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
				Map<String, Object> decipherRequest) throws IdAuthenticationAppException {
			// TODO Auto-generated method stub

		}
	};

	@SuppressWarnings("static-access")
	@Test(expected = IdAuthenticationAppException.class)
	public void encodetest() throws IdAuthenticationAppException {
		PowerMockito.mockStatic(CryptoUtil.class);
		String stringToDecode = "assad";
		Mockito.when(CryptoUtil.encodeBase64String(stringToDecode.getBytes()))
				.thenThrow(new IllegalArgumentException());
		baseAuthFilter.encode(stringToDecode);
	}

	@SuppressWarnings("static-access")
	@Test(expected = IdAuthenticationAppException.class)
	public void decodetest() throws IdAuthenticationAppException {
		PowerMockito.mockStatic(CryptoUtil.class);
		String stringToDecode = "assad";
		Mockito.when(CryptoUtil.decodeBase64(stringToDecode)).thenThrow(new IllegalArgumentException());
		baseAuthFilter.decode(stringToDecode);
	}
}
