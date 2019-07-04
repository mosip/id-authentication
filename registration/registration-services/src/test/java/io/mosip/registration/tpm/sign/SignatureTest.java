package io.mosip.registration.tpm.sign;

import java.nio.BufferUnderflowException;

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
import tss.tpm.TPMS_SIGNATURE_RSASSA;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMT_TK_HASHCHECK;
import tss.tpm.TPMU_SIGNATURE;
import tss.tpm.TPMU_SIG_SCHEME;
import tss.tpm.TPM_HANDLE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConfig.class, TPMT_PUBLIC.class })
public class SignatureTest {

	@BeforeClass
	public static void mockTPMLogger() throws Exception {
		PowerMockito.mockStatic(AppConfig.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(AppConfig.class, "getLogger", Mockito.any(Class.class));

		PowerMockito.doNothing().when(mockedLogger, "info", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		PowerMockito.doNothing().when(mockedLogger, "error", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void signDataTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		TPMS_SIGNATURE_RSASSA mockedSignature = PowerMockito.mock(TPMS_SIGNATURE_RSASSA.class);
		mockedSignature.sig = "signedData".getBytes();
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		createPrimaryResponse.handle = PowerMockito.mock(TPM_HANDLE.class);

		PowerMockito.doReturn(createPrimaryResponse).when(mockedTPM, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());
		PowerMockito.doReturn(mockedSignature).when(mockedTPM, "Sign", Mockito.any(TPM_HANDLE.class),
				Mockito.anyString().getBytes(), Mockito.any(TPMU_SIG_SCHEME.class),
				Mockito.any(TPMT_TK_HASHCHECK.class));
		PowerMockito.doNothing().when(mockedTPM, "FlushContext", Mockito.any(TPM_HANDLE.class));

		Assert.assertArrayEquals(mockedSignature.sig, Signature.signData(mockedTPM, "dataToSign".getBytes()));
	}

	@Test
	public void validateSignatureTest() throws Exception {
		PowerMockito.mockStatic(TPMT_PUBLIC.class);

		TPMT_PUBLIC publicKey = PowerMockito.mock(TPMT_PUBLIC.class);

		PowerMockito.when(TPMT_PUBLIC.class, "fromTpm", Mockito.anyString().getBytes()).thenReturn(publicKey);
		PowerMockito.doReturn(true).when(publicKey, "validateSignature", Mockito.anyString().getBytes(),
				Mockito.any(TPMU_SIGNATURE.class));

		Assert.assertTrue(Signature.validateSignatureUsingPublicPart("signedData".getBytes(), "actualData".getBytes(),
				"publicPart".getBytes()));

	}

	@Test
	public void validateSignatureExceptionTest() throws Exception {
		PowerMockito.mockStatic(TPMT_PUBLIC.class);

		PowerMockito.doThrow(new BufferUnderflowException()).when(TPMT_PUBLIC.class, "fromTpm",
				Mockito.anyString().getBytes());

		Assert.assertFalse(Signature.validateSignatureUsingPublicPart("signedData".getBytes(), "actualData".getBytes(),
				"publicPart".getBytes()));

	}

	@Test
	public void getKeyTest() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);

		PowerMockito.doReturn(createPrimaryResponse).when(tpm, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());

		Assert.assertSame(createPrimaryResponse, Whitebox.invokeMethod(Signature.class, "getKey", tpm));
	}

}
