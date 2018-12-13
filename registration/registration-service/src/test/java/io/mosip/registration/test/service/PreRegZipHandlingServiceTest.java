package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.external.impl.PreRegZipHandlingServiceImpl;

public class PreRegZipHandlingServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private KeyGenerator keyGenerator;

	@InjectMocks
	private PreRegZipHandlingServiceImpl preRegZipHandlingServiceImpl;

	static byte[] preRegPacket;

	static byte[] preRegPacketEncrypted;

	static MosipSecurityMethod mosipSecurityMethod;

	@BeforeClass
	public static void initialize() throws IOException {
		URL url = PreRegZipHandlingServiceTest.class.getResource("/41861504659248.zip");
		File packetZipFile = new File(url.getFile());
		preRegPacket = FileUtils.readFileToByteArray(packetZipFile);
		mosipSecurityMethod = MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING;

	}

	@Test
	public void extractPreRegZipFileTest() throws RegBaseCheckedException, IOException {

		RegistrationDTO registrationDTO = preRegZipHandlingServiceImpl.extractPreRegZipFile(preRegPacket);

		assertNotNull(registrationDTO);

	}

	@Test
	public void encryptAndSavePreRegPacketTest() throws RegBaseCheckedException {

		PreRegistrationDTO preRegistrationDTO = encryptPacket();
		assertNotNull(preRegistrationDTO);
	}

	private PreRegistrationDTO encryptPacket() throws RegBaseCheckedException {

		ReflectionTestUtils.setField(preRegZipHandlingServiceImpl, "preRegPacketLocation", "..//PreRegPacketStore");
		ReflectionTestUtils.setField(preRegZipHandlingServiceImpl, "preRegLocationDateFormat", "dd-MMM-yyyy");

		byte[] decodedKey = Base64.getDecoder().decode("0E8BAAEB3CED73CBC9BF4964F321824A");
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(originalKey);

		PreRegistrationDTO preRegistrationDTO = preRegZipHandlingServiceImpl
				.encryptAndSavePreRegPacket("89149679063970", preRegPacket);
		return preRegistrationDTO;
	}

	@Test
	public void decryptPreRegPacketTest() throws RegBaseCheckedException {

		final byte[] decrypted = preRegZipHandlingServiceImpl.decryptPreRegPacket("0E8BAAEB3CED73CBC9BF4964F321824A",
				encryptPacket().getEncryptedPacket());
		assertNotNull(decrypted);
	}
}
