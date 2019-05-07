package io.mosip.registration.tpm.spi;

import org.junit.Assert;
import org.junit.Before;
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
import io.mosip.registration.tpm.asymmetric.AsymmetricDecryptionService;
import io.mosip.registration.tpm.asymmetric.AsymmetricEncryptionService;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.initialize.TPMInitialization;
import io.mosip.registration.tpm.sign.SignKeyCreationService;
import io.mosip.registration.tpm.sign.SignatureService;
import io.mosip.registration.tpm.sign.SignatureValidationService;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPMT_PUBLIC;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TPMLogger.class, TPMInitialization.class })
public class TPMServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private SignKeyCreationService signKeyCreationService;
	@Mock
	private SignatureService signatureService;
	@Mock
	private SignatureValidationService signatureValidationService;
	@Mock
	private AsymmetricEncryptionService encryptionService;
	@Mock
	private AsymmetricDecryptionService decryptionService;
	@InjectMocks
	private TPMService tpmService;

	@BeforeClass
	public static void mockTPMLogger() throws Exception {
		PowerMockito.mockStatic(TPMLogger.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(TPMLogger.class, "getLogger", Mockito.any(Class.class));
		PowerMockito.doNothing().when(mockedLogger, "info", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Before
	public void initializeTpm() throws Exception {
		PowerMockito.mockStatic(TPMLogger.class, TPMInitialization.class);

		Tpm tpm = PowerMockito.mock(Tpm.class);

		PowerMockito.doReturn(tpm).when(TPMInitialization.class, "getTPMInstance");
	}

	@Test
	public void signDataTest() {
		byte[] signedData = "signedData".getBytes();

		PowerMockito.when(signatureService.signData(Mockito.any(Tpm.class), Mockito.anyString().getBytes()))
				.thenReturn(signedData);

		Assert.assertArrayEquals(signedData, tpmService.signData("dataToSign".getBytes()));
	}

	@Test
	public void validateSignatureTest() {
		PowerMockito.when(signatureValidationService.validateSignatureUsingPublicPart(Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes(), Mockito.anyString().getBytes())).thenReturn(true);

		Assert.assertTrue(tpmService.validateSignatureUsingPublicPart("signedData".getBytes(), "actualData".getBytes(),
				"publicPart".getBytes()));
	}

	@Test
	public void asymmetricEncryptTest() {
		byte[] encryptedData = "encryptedData".getBytes();

		PowerMockito.when(encryptionService.encryptUsingTPM(Mockito.any(Tpm.class), Mockito.anyString().getBytes()))
				.thenReturn(encryptedData);

		Assert.assertArrayEquals(encryptedData, tpmService.asymmetricEncrypt(encryptedData));
	}

	@Test
	public void asymmetricDecryptTest() {
		byte[] decryptedData = "decryptedData".getBytes();

		PowerMockito.when(decryptionService.decryptUsingTPM(Mockito.any(Tpm.class), Mockito.anyString().getBytes()))
				.thenReturn(decryptedData);

		Assert.assertArrayEquals(decryptedData, tpmService.asymmetricDecrypt(decryptedData));
	}

	@Test
	public void getSigningPublicPartTest() {
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPMT_PUBLIC publicKey = PowerMockito.mock(TPMT_PUBLIC.class);
		createPrimaryResponse.outPublic = publicKey;
		byte[] outTpm = "signingPublicKey".getBytes();

		PowerMockito.when(signKeyCreationService.getKey(Mockito.any(Tpm.class))).thenReturn(createPrimaryResponse);
		PowerMockito.when(publicKey.toTpm()).thenReturn(outTpm);

		Assert.assertArrayEquals(outTpm, tpmService.getSigningPublicPart());
	}

}
