package io.mosip.authentication.common.service.transaction.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.kernel.zkcryptoservice.dto.CryptoDataDto;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.repository.DataEncryptKeystoreRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.signature.dto.JWTSignatureRequestDto;
import io.mosip.kernel.signature.dto.JWTSignatureResponseDto;
import io.mosip.kernel.signature.dto.JWTSignatureVerifyResponseDto;
import io.mosip.kernel.signature.service.SignatureService;
import io.mosip.kernel.zkcryptoservice.dto.ReEncryptRandomKeyResponseDto;
import io.mosip.kernel.zkcryptoservice.dto.ZKCryptoResponseDto;
import io.mosip.kernel.zkcryptoservice.service.spi.ZKCryptoManagerService;
import io.netty.util.internal.ReflectionUtil;

/**
 * 
 * @author Loganathan Sekar
 * @author Manoj SP
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, RestRequestFactory.class,
		ObjectMapper.class, RestRequestFactory.class })
@Import(EnvUtil.class)
public class IdAuthSecurityManagerTest {

	@Mock
	private CryptomanagerService cryptomanagerService;

	@Mock
	private KeymanagerService keyManager;

	@InjectMocks
	IdAuthSecurityManager authSecurityManager;

	@Mock
	private ZKCryptoManagerService zkCryptoManagerService;

	@Mock
	private DataEncryptKeystoreRepository repo;

	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private CryptoCore cryptoCore;

	@Mock
	private SignatureService signatureService;

	@Mock
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	@Mock
	private IdentityCacheRepository identityRepo;
	
	@Mock
	private IdTypeUtil idTypeUtil;

	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	@Value("${" + IdAuthConfigKeyConstants.KEY_SPLITTER + "}")
	private String keySplitter;

	private int tokenIDLength;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authSecurityManager, "signApplicationid", "22");
		ReflectionTestUtils.setField(authSecurityManager, "signRefid", "12");
		ReflectionTestUtils.setField(authSecurityManager, "keySplitter", "test");
		ReflectionTestUtils.setField(authSecurityManager, "tokenIDLength", 4);
	}

	@Test
	public void testEncrypt() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.encrypt(Mockito.any()))
				.thenReturn(new CryptomanagerResponseDto(CryptoUtil.encodeBase64("abcd".getBytes())));
		byte[] encrypt = authSecurityManager.encrypt("Hello", "20190101", null, null);
		assertEquals("abcd", new String(encrypt));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testEncryptNoUniqueAliasException() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.encrypt(Mockito.any())).thenThrow(new NoUniqueAliasException("", ""));
		authSecurityManager.encrypt("Hello", "20190101", null, null);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testEncryptException() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.encrypt(Mockito.any())).thenThrow(new BaseUncheckedException("", ""));
		authSecurityManager.encrypt("Hello", "20190101", null, null);
	}

	@Test
	public void testDecrypt() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.decrypt(Mockito.any()))
				.thenReturn(new CryptomanagerResponseDto(CryptoUtil.encodeBase64("abcd".getBytes())));
		byte[] decrypt = authSecurityManager.decrypt("Hello", "20190101", null, null, false);
		assertEquals("abcd", new String(decrypt));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testDecryptNoUniqueAliasException() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.decrypt(Mockito.any())).thenThrow(new NoUniqueAliasException("", ""));
		authSecurityManager.decrypt("Hello", "20190101", null, null, false);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testDecryptException() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.decrypt(Mockito.any())).thenThrow(new BaseUncheckedException("", ""));
		authSecurityManager.decrypt("Hello", "20190101", null, null, false);
	}

//	@Test
//	public void testSign() throws IdAuthenticationBusinessException {
//		when(keyManager.sign(Mockito.any())).thenReturn(new SignatureResponseDto("abcd"));
//		String sign = authSecurityManager.sign("req");
//		assertEquals("abcd", sign);
//	}

	@Test
	public void testTrimBeginEndForKey() {
		String pkey = "-----BEGIN RSA PRIVATE KEY-----\r\n"
				+ "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31u\r\n"
				+ "FpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDf\r\n"
				+ "WGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==\r\n"
				+ "-----END RSA PRIVATE KEY-----\r\n";
		String out = authSecurityManager.trimBeginEnd(pkey);
		String actual_out = "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31uFpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDfWGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==";
		assertEquals(actual_out, out);
	}

	@Test
	public void testTrimBeginEndForCert() {
		String pkey = "-----BEGIN RSA PRIVATE KEY-----\r\n"
				+ "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31u\r\n"
				+ "FpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDf\r\n"
				+ "WGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==\r\n"
				+ "-----END RSA PRIVATE KEY-----\r\n";
		String out = authSecurityManager.trimBeginEnd(pkey);
		String actual_out = "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31uFpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDfWGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==";
		assertEquals(actual_out, out);
	}

	@Test
	public void testTrimBeginEndForCertException() {
		String pkey = "--------------------------------BEGIN RSA PRIVATE KEY--------------------------------\r\n"
				+ "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31u\r\n"
				+ "FpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDf\r\n"
				+ "WGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==\r\n"
				+ "--------------------------------END RSA PRIVATE KEY--------------------------------\r\n";
		String out = authSecurityManager.trimBeginEnd(pkey);
		String actual_out = "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31uFpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDfWGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==";
		assertNotSame(actual_out, out);
	}

	@Test
	public void reEncryptRandomKeyTest() {
		String encryptedKey = "Ahfsd";
		ReEncryptRandomKeyResponseDto zkReEncryptRandomKeyRespDto = new ReEncryptRandomKeyResponseDto();
		Mockito.when(zkCryptoManagerService.zkReEncryptRandomKey(Mockito.any()))
				.thenReturn(zkReEncryptRandomKeyRespDto);
		String response = authSecurityManager.reEncryptRandomKey(encryptedKey);
		assertEquals(response, zkReEncryptRandomKeyRespDto.getEncryptedKey());
	}

	@Test
	public void reEncryptAndStoreRandomKeyTest() {
		String index = "1";
		String key = "Ahg";
		ReEncryptRandomKeyResponseDto zkReEncryptRandomKeyRespDto = new ReEncryptRandomKeyResponseDto();
		Mockito.when(zkCryptoManagerService.zkReEncryptRandomKey(Mockito.any()))
				.thenReturn(zkReEncryptRandomKeyRespDto);
		authSecurityManager.reEncryptAndStoreRandomKey(index, key);
	}

	@Test
	public void zkDecryptTest() throws IdAuthenticationBusinessException {
		String id = "1";
		Map<String, String> encryptedAttributes = new HashMap<String, String>();
		ZKCryptoResponseDto zkDecryptResponse = new ZKCryptoResponseDto();
		List<CryptoDataDto> zkDataAttributes = new ArrayList<CryptoDataDto>();
		zkDecryptResponse.setZkDataAttributes(zkDataAttributes);
		encryptedAttributes.put("key1", "value1");
		encryptedAttributes.put("key2", "value2");
		Mockito.when(zkCryptoManagerService.zkDecrypt(Mockito.any())).thenReturn(zkDecryptResponse);
		Map<String, String> response = authSecurityManager.zkDecrypt(id, encryptedAttributes);
		assertNotNull(response);
	}

	@Test
	public void generateHashAndDigestAsPlainTextTest() {
		byte[] data = "Test".getBytes();
		String actualResponse = "532EAABD9574880DBF76B9B8CC00832C20A6EC113D682299550D7A6E0F345E25";
		String response = IdAuthSecurityManager.generateHashAndDigestAsPlainText(data);
		assertEquals(response, actualResponse);
	}

	@Test
	public void createRandomTokenTest() throws IdAuthenticationBusinessException {
		byte[] dataToEncrypt = "Test".getBytes();
		byte[] encryptedData = "Test".getBytes();
		Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(encryptedData);
		String actualResponse = "2786";
		String response = authSecurityManager.createRandomToken(dataToEncrypt);
		assertEquals(response, actualResponse);
	}

	@Test
	public void signTest() {
		JWTSignatureResponseDto responseDto = new JWTSignatureResponseDto();
		JWTSignatureRequestDto request = new JWTSignatureRequestDto();
		String data = "Test";
		request.setApplicationId("123");
		request.setCertificateUrl("https://test");
		request.setIncludeCertHash(true);
		request.setReferenceId("12");
		request.setDataToSign(data);
		request.setIncludeCertificate(true);
		request.setIncludePayload(true);
		responseDto.setJwtSignedData(data);
		Mockito.when(signatureService.jwtSign(Mockito.any())).thenReturn(responseDto);
		String response = authSecurityManager.sign(data);
		assertEquals(response, data);
	}

	@Test
	public void verifySignatureTest() {
		JWTSignatureVerifyResponseDto jwtResponse = new JWTSignatureVerifyResponseDto();
		jwtResponse.setTrustValid("Test");
		jwtResponse.setSignatureValid(true);
		jwtResponse.setMessage("Demo");
		String signature = "Test";
		String domain = "https://test";
		String requestData = "Test";
		Boolean isTrustValidationRequired = false;
		Mockito.when(signatureService.jwtVerify(Mockito.any())).thenReturn(jwtResponse);
		Boolean response = authSecurityManager.verifySignature(signature, domain, requestData,
				isTrustValidationRequired);
		assertEquals(response, true);
	}

	@Test
	public void verifySignatureTrustValidationTest() {
		JWTSignatureVerifyResponseDto jwtResponse = new JWTSignatureVerifyResponseDto();
		jwtResponse.setTrustValid("Test");
		jwtResponse.setSignatureValid(true);
		jwtResponse.setMessage("Demo");
		String signature = "Test";
		String domain = "https://test";
		String requestData = "Test";
		Boolean isTrustValidationRequired = true;
		Mockito.when(signatureService.jwtVerify(Mockito.any())).thenReturn(jwtResponse);
		Boolean response = authSecurityManager.verifySignature(signature, domain, requestData,
				isTrustValidationRequired);
		assertEquals(response, false);
	}

	@Test
	public void hashTest() throws IdAuthenticationBusinessException {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.any())).thenReturn(id);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById(Mockito.anyString())).thenReturn(true);
		String response = authSecurityManager.hash(id);
		assertEquals(response, actualResponse);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashTest_salt_key_not_exists() throws IdAuthenticationBusinessException {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.any())).thenReturn(null);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById(Mockito.anyString())).thenReturn(true);
		String response = authSecurityManager.hash(id);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashTest_salt_key_not_exists_legacy_hash_enabled() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.any())).thenReturn(null);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(false);
		String response = authSecurityManager.hash(id);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", true);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashTest_salt_key_not_exists_legacy_hash_disabled() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.any())).thenReturn(null);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(false);
		String response = authSecurityManager.hash(id);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}
	
	@Test
	public void hashTestLegacy_newIdNotExists_legacyEnabled() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(328)).thenReturn("328");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(12)).thenReturn(id);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("827050EF00E06C5547A64C9208F244B9B96CFABEB043F6D2ADBC4142FC1B39B2")).thenReturn(false);
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(true);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", true);
		String response = authSecurityManager.hash(id);
		assertEquals(response, actualResponse);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}
	
	@Test
	public void hashTestLegacy_newIdNotExists_legacyEnabled_newSaltKeyNotExists() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(328)).thenReturn(null);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(12)).thenReturn(id);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("827050EF00E06C5547A64C9208F244B9B96CFABEB043F6D2ADBC4142FC1B39B2")).thenReturn(false);
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(true);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", true);
		String response = authSecurityManager.hash(id);
		assertEquals(response, actualResponse);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashTestLegacy_newIdNotExists_legacyDisabled_newSaltKeyNotExists() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(328)).thenReturn(null);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(12)).thenReturn(id);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("827050EF00E06C5547A64C9208F244B9B96CFABEB043F6D2ADBC4142FC1B39B2")).thenReturn(false);
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(true);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
		String response = authSecurityManager.hash(id);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashTestLegacy_newIdExists_legacyEnabled_legacyHashDoesNotExists() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(328)).thenReturn("328");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(12)).thenReturn(id);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("827050EF00E06C5547A64C9208F244B9B96CFABEB043F6D2ADBC4142FC1B39B2")).thenReturn(false);
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(false);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", true);
		String response = authSecurityManager.hash(id);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashTestLegacy_newIdNotExists_legacyEnabled_legacySaltKeyNotExists() throws IdAuthenticationBusinessException {
		try {
		String id = "12";
		Mockito.when(uinHashSaltRepo.retrieveSaltById(328)).thenReturn("328");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(12)).thenReturn(null);
		String actualResponse = "CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481";
		Mockito.when(identityRepo.existsById("827050EF00E06C5547A64C9208F244B9B96CFABEB043F6D2ADBC4142FC1B39B2")).thenReturn(false);
		Mockito.when(identityRepo.existsById("CBFAD02F9ED2A8D1E08D8F74F5303E9EB93637D47F82AB6F1C15871CF8DD0481")).thenReturn(true);
		ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", true);
		String response = authSecurityManager.hash(id);
		} catch (Exception e) {
			ReflectionTestUtils.setField(authSecurityManager, "legacySaltSelectionEnabled", false);
			throw e;
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void hashExceptionTest() throws IdAuthenticationBusinessException {
		String id = "12";
		authSecurityManager.hash(id);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void encryptDataExceptionTest() throws IdAuthenticationBusinessException {
		byte[] data = "Test".getBytes();
		String partnerCertificate = "Test";
		authSecurityManager.encryptData(data, partnerCertificate);
	}

	@Test
	public void combineDataToEncryptTest() {
		byte[] encryptedData = "Test".getBytes();
		byte[] encryptedSymmetricKey = "Demo".getBytes();
		byte[] response = authSecurityManager.combineDataToEncrypt(encryptedData, encryptedSymmetricKey);
		assertNotNull(response);
	}

	@Test
	public void getBytesFromThumbprintTest() {
		String thumbprint = "Test";
		byte[] response = IdAuthSecurityManager.getBytesFromThumbprint(thumbprint);
		assertNotNull(response);
	}

}
