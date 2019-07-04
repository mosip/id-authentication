package io.mosip.registration.tpm.asymmetric;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.registration.config.AppConfig;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMU_ASYM_SCHEME;
import tss.tpm.TPM_HANDLE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConfig.class })
public class RSACipherTest {

	@BeforeClass
	public static void mockTPMLogger() throws Exception {
		PowerMockito.mockStatic(AppConfig.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(AppConfig.class, "getLogger", Mockito.any(Class.class));

		PowerMockito.doNothing().when(mockedLogger, "info", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void encryptTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		byte[] encryptedData = "encrypted".getBytes();
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPM_HANDLE tpmHandle = PowerMockito.mock(TPM_HANDLE.class);
		createPrimaryResponse.handle = tpmHandle;

		PowerMockito.doReturn(createPrimaryResponse).when(mockedTPM, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());

		PowerMockito.when(mockedTPM.RSA_Encrypt(Mockito.any(TPM_HANDLE.class), Mockito.anyString().getBytes(),
				Mockito.any(TPMU_ASYM_SCHEME.class), Mockito.anyString().getBytes())).thenReturn(encryptedData);

		Assert.assertArrayEquals(encryptedData, RSACipher.encrypt(mockedTPM, "dataToEncrypt".getBytes()));
	}

	@Test
	public void decryptTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		byte[] decryptedData = "decrypted".getBytes();
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPM_HANDLE tpmHandle = PowerMockito.mock(TPM_HANDLE.class);
		createPrimaryResponse.handle = tpmHandle;

		PowerMockito.doReturn(createPrimaryResponse).when(mockedTPM, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());

		PowerMockito.when(mockedTPM.RSA_Decrypt(Mockito.any(TPM_HANDLE.class), Mockito.anyString().getBytes(),
				Mockito.any(TPMU_ASYM_SCHEME.class), Mockito.anyString().getBytes())).thenReturn(decryptedData);

		Assert.assertArrayEquals(decryptedData, RSACipher.decrypt(mockedTPM, "dataToEncrypt".getBytes()));
	}

	@Test
	public void createRSAKeyTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPM_HANDLE tpmHandle = PowerMockito.mock(TPM_HANDLE.class);
		createPrimaryResponse.handle = tpmHandle;

		PowerMockito.doReturn(createPrimaryResponse).when(mockedTPM, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());

		Assert.assertEquals(tpmHandle, Whitebox.invokeMethod(RSACipher.class, "createRSAKey", mockedTPM));
	}

}
