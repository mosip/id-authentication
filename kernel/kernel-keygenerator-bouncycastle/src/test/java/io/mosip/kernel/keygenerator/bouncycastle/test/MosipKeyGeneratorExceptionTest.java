package io.mosip.kernel.keygenerator.bouncycastle.test;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.keygenerator.bouncycastle.MosipKeyGenerator;
import io.mosip.kernel.keygenerator.bouncycastle.constant.KeyGeneratorExceptionConstant;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MosipKeyGenerator.class)
public class MosipKeyGeneratorExceptionTest {

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testGetAsymmetricKeyException() {
		PowerMockito.mockStatic(MosipKeyGenerator.class);
		when(MosipKeyGenerator.getAsymmetricKey()).thenThrow(new MosipNoSuchAlgorithmException(
				KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
				KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage()));
		MosipKeyGenerator.getAsymmetricKey();
	}

}
