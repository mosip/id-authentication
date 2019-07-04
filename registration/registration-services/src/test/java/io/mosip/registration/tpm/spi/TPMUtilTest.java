package io.mosip.registration.tpm.spi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.tpm.asymmetric.RSACipher;
import io.mosip.registration.tpm.initialize.TPMInitialization;
import io.mosip.registration.tpm.sign.Signature;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPMT_PUBLIC;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConfig.class, TPMInitialization.class, RSACipher.class, Signature.class })
public class TPMUtilTest {

	@BeforeClass
	public static void mockLogger() throws Exception {
		PowerMockito.mockStatic(AppConfig.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(AppConfig.class, "getLogger", Mockito.any(Class.class));

		PowerMockito.doNothing().when(mockedLogger, "info", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		PowerMockito.doNothing().when(mockedLogger, "error", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Before
	public void initializeTpm() throws Exception {
		PowerMockito.mockStatic(TPMInitialization.class);

		Tpm tpm = PowerMockito.mock(Tpm.class);

		PowerMockito.doReturn(tpm).when(TPMInitialization.class, "getTPMInstance");
	}

	@Test
	public void signDataTest() throws Exception {
		byte[] signedData = "signedData".getBytes();
		PowerMockito.mockStatic(Signature.class);

		PowerMockito.doReturn(signedData).when(Signature.class, "signData", Mockito.any(Tpm.class),
				Mockito.anyString().getBytes());

		Assert.assertArrayEquals(signedData, TPMUtil.signData("dataToSign".getBytes()));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void signDataExceptionTest() throws Exception {
		PowerMockito.mockStatic(Signature.class);

		PowerMockito.doThrow(new RuntimeException()).when(Signature.class, "signData", Mockito.any(Tpm.class),
				Mockito.anyString().getBytes());

		TPMUtil.signData("dataToSign".getBytes());
	}

	@Test
	public void validateSignatureTest() throws Exception {
		PowerMockito.mockStatic(Signature.class);

		PowerMockito.doReturn(true).when(Signature.class, "validateSignatureUsingPublicPart",
				Mockito.anyString().getBytes(), Mockito.anyString().getBytes(), Mockito.anyString().getBytes());

		Assert.assertTrue(
				TPMUtil.validateSignature("signedData".getBytes(), "actualData".getBytes(), "publicPart".getBytes()));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void validateSignatureExceptionTest() throws Exception {
		PowerMockito.mockStatic(Signature.class);

		PowerMockito.doThrow(new RuntimeException()).when(Signature.class, "validateSignatureUsingPublicPart",
				Mockito.anyString().getBytes(), Mockito.anyString().getBytes(), Mockito.anyString().getBytes());

		TPMUtil.validateSignature("signedData".getBytes(), "actualData".getBytes(), "publicPart".getBytes());
	}

	@Test
	public void asymmetricEncryptTest() throws Exception {
		PowerMockito.mockStatic(RSACipher.class);

		byte[] encryptedData = "encryptedData".getBytes();

		PowerMockito.doReturn(encryptedData).when(RSACipher.class, "encrypt", Mockito.any(Tpm.class),
				Mockito.anyString().getBytes());

		Assert.assertArrayEquals(encryptedData, TPMUtil.asymmetricEncrypt(encryptedData));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void asymmetricEncryptExceptionTest() throws Exception {
		PowerMockito.mockStatic(RSACipher.class);

		byte[] encryptedData = "encryptedData".getBytes();

		PowerMockito.doThrow(new RuntimeException()).when(RSACipher.class, "encrypt", Mockito.any(Tpm.class),
				Mockito.anyString().getBytes());

		TPMUtil.asymmetricEncrypt(encryptedData);
	}

	@Test
	public void asymmetricDecryptTest() throws Exception {
		PowerMockito.mockStatic(RSACipher.class);

		byte[] decryptedData = "decryptedData".getBytes();

		PowerMockito.doReturn(decryptedData).when(RSACipher.class, "decrypt", Mockito.any(Tpm.class),
				Mockito.anyString().getBytes());

		Assert.assertArrayEquals(decryptedData, TPMUtil.asymmetricDecrypt(decryptedData));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void asymmetricDecryptExceptionTest() throws Exception {
		PowerMockito.mockStatic(RSACipher.class);

		byte[] decryptedData = "decryptedData".getBytes();

		PowerMockito.doThrow(new RuntimeException()).when(RSACipher.class, "decrypt", Mockito.any(Tpm.class),
				Mockito.anyString().getBytes());

		TPMUtil.asymmetricDecrypt(decryptedData);
	}

	@Test
	public void getSigningPublicPartTest() throws Exception {
		PowerMockito.mockStatic(Signature.class);

		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPMT_PUBLIC publicKey = PowerMockito.mock(TPMT_PUBLIC.class);
		createPrimaryResponse.outPublic = publicKey;
		byte[] outTpm = "signingPublicKey".getBytes();

		PowerMockito.doReturn(createPrimaryResponse).when(Signature.class, "getKey", Mockito.any(Tpm.class));
		PowerMockito.when(publicKey.toTpm()).thenReturn(outTpm);

		Assert.assertArrayEquals(outTpm, TPMUtil.getSigningPublicPart());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getSigningPublicPartExceptionTest() throws Exception {
		PowerMockito.mockStatic(Signature.class);

		PowerMockito.doThrow(new RuntimeException()).when(Signature.class, "getKey", Mockito.any(Tpm.class));

		TPMUtil.getSigningPublicPart();
	}

	@Test
	public void getTPMInstanceTest() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);

		PowerMockito.doReturn(tpm).when(TPMInitialization.class, "getTPMInstance");

		Assert.assertEquals(tpm, TPMUtil.getTPMInstance());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void getTPMInstanceExceptionTest() throws Exception {
		PowerMockito.doThrow(new RuntimeException()).when(TPMInitialization.class, "getTPMInstance");

		TPMUtil.getTPMInstance();
	}

	@Test
	public void closeTPMInstanceTest() throws Exception {
		PowerMockito.doNothing().when(TPMInitialization.class, "closeTPMInstance");

		TPMUtil.closeTPMInstance();
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void closeTPMInstanceExceptionTest() throws Exception {
		PowerMockito.doThrow(new RuntimeException()).when(TPMInitialization.class, "closeTPMInstance");

		TPMUtil.closeTPMInstance();
	}

}
