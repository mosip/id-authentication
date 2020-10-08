package io.mosip.authentication.common.service.transaction.manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.signature.dto.SignatureResponseDto;

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
public class IdAuthSecurityManagerTest {

	@Mock
	private CryptomanagerService cryptomanagerService;

	@Mock
	private KeymanagerService keyManager;

	@InjectMocks
	IdAuthSecurityManager authSecurityManager;

	@Autowired
	private Environment environment;

	@Before
	public void init() throws IdAuthenticationBusinessException {
		ReflectionTestUtils.setField(authSecurityManager, "env", environment);
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
		byte[] decrypt = authSecurityManager.decrypt("Hello", "20190101", null, null);
		assertEquals("abcd", new String(decrypt));
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testDecryptNoUniqueAliasException() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.decrypt(Mockito.any())).thenThrow(new NoUniqueAliasException("", ""));
		authSecurityManager.decrypt("Hello", "20190101", null, null);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testDecryptException() throws IdAuthenticationBusinessException {
		when(cryptomanagerService.decrypt(Mockito.any())).thenThrow(new BaseUncheckedException("", ""));
		authSecurityManager.decrypt("Hello", "20190101", null, null);
	}

//	@Test
//	public void testSign() throws IdAuthenticationBusinessException {
//		when(keyManager.sign(Mockito.any())).thenReturn(new SignatureResponseDto("abcd"));
//		String sign = authSecurityManager.sign("req");
//		assertEquals("abcd", sign);
//	}

}
