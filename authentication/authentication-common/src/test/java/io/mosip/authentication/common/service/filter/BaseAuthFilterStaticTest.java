package io.mosip.authentication.common.service.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.logger.logback.factory.Logfactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CryptoUtil.class, IdaLogger.class, Logfactory.class })
public class BaseAuthFilterStaticTest {

	@Test(expected = IdAuthenticationAppException.class)
	public void decodetest() throws IdAuthenticationAppException {
		PowerMockito.mockStatic(CryptoUtil.class);
		PowerMockito.mockStatic(IdaLogger.class);
		String stringToDecode = "assad";
		Mockito.when(CryptoUtil.decodeBase64(stringToDecode)).thenThrow(new IllegalArgumentException());
		Mockito.when(IdaLogger.getLogger(Mockito.any())).thenReturn(Mockito.mock(Logger.class));
		BaseAuthFilter.decode(stringToDecode);
	}
}
