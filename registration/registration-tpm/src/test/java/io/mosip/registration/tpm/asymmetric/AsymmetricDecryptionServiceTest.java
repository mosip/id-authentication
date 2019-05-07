package io.mosip.registration.tpm.asymmetric;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.registration.tpm.config.TPMLogger;

import tss.Tpm;
import tss.tpm.TPMU_ASYM_SCHEME;
import tss.tpm.TPM_HANDLE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TPMLogger.class })
public class AsymmetricDecryptionServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AsymmetricKeyCreationService asymmetricKeyCreationService;
	@InjectMocks
	private AsymmetricDecryptionService asymmetricDecryptionService;

	@BeforeClass
	public static void mockTPMLogger() throws Exception {
		PowerMockito.mockStatic(TPMLogger.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(TPMLogger.class, "getLogger", Mockito.any(Class.class));

		PowerMockito.doNothing().when(mockedLogger, "info", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void decryptTest() {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		byte[] decryptedData = "decrypted".getBytes();

		PowerMockito.when(asymmetricKeyCreationService.createPersistentKey(Mockito.any(Tpm.class)))
				.thenReturn(PowerMockito.mock(TPM_HANDLE.class));

		PowerMockito.when(mockedTPM.RSA_Decrypt(Mockito.any(TPM_HANDLE.class), Mockito.anyString().getBytes(),
				Mockito.any(TPMU_ASYM_SCHEME.class), Mockito.anyString().getBytes())).thenReturn(decryptedData);

		Assert.assertArrayEquals(decryptedData,
				asymmetricDecryptionService.decryptUsingTPM(mockedTPM, "encryptedData".getBytes()));
	}

}
