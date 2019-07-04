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

public class AsymmetricEncryptionServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AsymmetricKeyCreationService asymmetricKeyCreationService;
	@InjectMocks
	private AsymmetricEncryptionService asymmetricEncryptionService;

	@Test
	public void encryptTest() {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		byte[] encryptedData = "encrypted".getBytes();

		PowerMockito.when(asymmetricKeyCreationService.createPersistentKey(Mockito.any(Tpm.class)))
				.thenReturn(PowerMockito.mock(TPM_HANDLE.class));

		PowerMockito.when(mockedTPM.RSA_Encrypt(Mockito.any(TPM_HANDLE.class), Mockito.anyString().getBytes(),
				Mockito.any(TPMU_ASYM_SCHEME.class), Mockito.anyString().getBytes())).thenReturn(encryptedData);

		Assert.assertArrayEquals(encryptedData,
				asymmetricEncryptionService.encryptUsingTPM(mockedTPM, "dataToEncrypt".getBytes()));
	}

}
