package io.mosip.registration.tpm.sign;

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

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.registration.tpm.config.TPMLogger;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPMS_SIGNATURE_RSASSA;
import tss.tpm.TPMT_TK_HASHCHECK;
import tss.tpm.TPMU_SIG_SCHEME;
import tss.tpm.TPM_HANDLE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TPMLogger.class })
public class SignatureServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private SignKeyCreationService signKeyCreationService;
	@InjectMocks
	private SignatureService signatureService;

	@BeforeClass
	public static void mockTPMLogger() throws Exception {
		PowerMockito.mockStatic(TPMLogger.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(TPMLogger.class, "getLogger", Mockito.any(Class.class));

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

		PowerMockito.when(signKeyCreationService.getKey(Mockito.any(Tpm.class))).thenReturn(createPrimaryResponse);
		PowerMockito.doReturn(mockedSignature).when(mockedTPM, "Sign", Mockito.any(TPM_HANDLE.class),
				Mockito.anyString().getBytes(), Mockito.any(TPMU_SIG_SCHEME.class),
				Mockito.any(TPMT_TK_HASHCHECK.class));
		PowerMockito.doNothing().when(mockedTPM, "FlushContext", Mockito.any(TPM_HANDLE.class));

		Assert.assertArrayEquals(mockedSignature.sig, signatureService.signData(mockedTPM, "dataToSign".getBytes()));

	}

	@Test(expected = BaseUncheckedException.class)
	public void signDataRuntimeExceptionTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);

		PowerMockito.when(signKeyCreationService.getKey(Mockito.any(Tpm.class))).thenThrow(new RuntimeException());

		signatureService.signData(mockedTPM, "dataToSign".getBytes());

	}

}
