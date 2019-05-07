package io.mosip.registration.tpm.sign;

import java.nio.BufferUnderflowException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.registration.tpm.config.TPMLogger;

import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMU_SIGNATURE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TPMLogger.class, TPMT_PUBLIC.class })
public class SignatureValidationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private SignatureValidationService signatureValidationService;

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
	public void validateSignatureTest() throws Exception {
		PowerMockito.mockStatic(TPMT_PUBLIC.class);

		TPMT_PUBLIC publicKey = PowerMockito.mock(TPMT_PUBLIC.class);

		PowerMockito.when(TPMT_PUBLIC.class, "fromTpm", Mockito.anyString().getBytes()).thenReturn(publicKey);
		PowerMockito.doReturn(true).when(publicKey, "validateSignature", Mockito.anyString().getBytes(),
				Mockito.any(TPMU_SIGNATURE.class));

		Assert.assertTrue(signatureValidationService.validateSignatureUsingPublicPart("signedData".getBytes(),
				"actualData".getBytes(), "publicPart".getBytes()));

	}

	@Test
	public void validateSignatureExceptionTest() throws Exception {
		PowerMockito.mockStatic(TPMT_PUBLIC.class);

		PowerMockito.doThrow(new BufferUnderflowException()).when(TPMT_PUBLIC.class, "fromTpm",
				Mockito.anyString().getBytes());

		Assert.assertFalse(signatureValidationService.validateSignatureUsingPublicPart("signedData".getBytes(),
				"actualData".getBytes(), "publicPart".getBytes()));

	}

}
