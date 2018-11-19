package io.mosip.kernel.keygenerator.bouncycastle.test;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keygenerator.bouncycastle.constant.KeyGeneratorExceptionConstant;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KeyGenerator.class)
public class KeyGeneratorExceptionTest {

	@Test(expected = NoSuchAlgorithmException.class)
	public void testGetAsymmetricKeyException() {
		PowerMockito.mockStatic(KeyGenerator.class);
		when(KeyGenerator.getAsymmetricKey()).thenThrow(new NoSuchAlgorithmException(
				KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
				KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage()));
		KeyGenerator.getAsymmetricKey();
	}

}
