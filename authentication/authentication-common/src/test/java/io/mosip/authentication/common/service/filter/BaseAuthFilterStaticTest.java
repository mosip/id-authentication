package io.mosip.authentication.common.service.filter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.util.CryptoUtil;

@Disabled
@Import(EnvUtil.class)
public class BaseAuthFilterStaticTest {

	@Test
	public void decodetest() throws IdAuthenticationAppException {
		try (MockedStatic<CryptoUtil> cryptoUtil = Mockito.mockStatic(CryptoUtil.class)) {
			String stringToDecode = "YWJjZA==";
			cryptoUtil.when(() -> CryptoUtil.decodeBase64Url(stringToDecode)).thenThrow(new IllegalArgumentException());
			assertThrows(IdAuthenticationAppException.class, () -> BaseAuthFilter.decode(stringToDecode));
		}
	}
}
