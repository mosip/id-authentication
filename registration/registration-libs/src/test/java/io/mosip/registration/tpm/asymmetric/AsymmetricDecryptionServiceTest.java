package io.mosip.registration.tpm.asymmetric;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;

import tss.Tpm;
import tss.tpm.TPMU_ASYM_SCHEME;
import tss.tpm.TPM_HANDLE;

public class AsymmetricDecryptionServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AsymmetricKeyCreationService asymmetricKeyCreationService;
	@InjectMocks
	private AsymmetricDecryptionService asymmetricDecryptionService;

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
