package io.mosip.authentication.common.service.transaction.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;

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

	@Before
	public void init() throws IdAuthenticationBusinessException {
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
				+"WGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==\r\n"
				+ "-----END RSA PRIVATE KEY-----\r\n";
		String out  = authSecurityManager.trimBeginEnd(pkey);
		String actual_out = "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31uFpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDfWGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==";
		assertEquals(actual_out,out);
	}
	
	@Test
	public void testTrimBeginEndForCert() {
		String pkey = "-----BEGIN RSA PRIVATE KEY-----\r\n"
				+ "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31u\r\n"
				+ "FpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDf\r\n"
				+"WGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==\r\n"
				+ "-----END RSA PRIVATE KEY-----\r\n";
		String out  = authSecurityManager.trimBeginEnd(pkey);
		String actual_out = "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31uFpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDfWGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==";
		assertEquals(actual_out,out);
	}

	@Test
	public void testTrimBeginEndForCertException(){
		String pkey = "--------------------------------BEGIN RSA PRIVATE KEY--------------------------------\r\n"
				+ "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31u\r\n"
				+ "FpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDf\r\n"
				+"WGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==\r\n"
				+ "--------------------------------END RSA PRIVATE KEY--------------------------------\r\n";
		String out  = authSecurityManager.trimBeginEnd(pkey);
		String actual_out = "MIIJJwIBAAKCAgEAsYTVyPeMD2SyaQTqTnpl59P0OEJwCjS1MsFWEOhGK5wg/31uFpKHjJ8RrQe++Lb00c8sTuYrjjL5eK4JwDjCRXP2PZWfBzts2HX0eCpk5n7YSgDfWGVSRPDlVFIWKIsgZhwYbihzf2YJHE0DlcQgaDrsG8ZLVQ4Sy5DnhO32vA==";
		assertNotSame(actual_out,out);
	}
	

}
