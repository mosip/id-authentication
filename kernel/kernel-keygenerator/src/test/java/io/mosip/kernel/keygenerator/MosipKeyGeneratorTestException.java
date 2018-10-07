package io.mosip.kernel.keygenerator;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import io.mosip.kernel.keygenerator.constants.KeyGeneratorExceptionConstants;
import io.mosip.kernel.keygenerator.exception.MosipNoSuchAlgorithmException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MosipKeyGenerator.class)
public class MosipKeyGeneratorTestException {
	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testGetAsymmetricKeyException() {
        PowerMockito.mockStatic(MosipKeyGenerator.class);
		when(MosipKeyGenerator.getAsymmetricKey()).thenThrow(
				new MosipNoSuchAlgorithmException(KeyGeneratorExceptionConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION));
		MosipKeyGenerator.getAsymmetricKey();
	}


}
