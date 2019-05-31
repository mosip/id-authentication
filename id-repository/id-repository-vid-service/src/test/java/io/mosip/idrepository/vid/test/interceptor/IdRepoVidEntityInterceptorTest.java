package io.mosip.idrepository.vid.test.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.interceptor.IdRepoVidEntityInterceptor;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoVidEntityInterceptorTest {

	@Mock
	private IdRepoSecurityManager securityManager;

	@InjectMocks
	private IdRepoVidEntityInterceptor interceptor;

	@Test
	@Ignore
	public void testOnSave() throws IdRepoAppException {
		when(securityManager.encrypt(Mockito.any())).thenReturn("".getBytes());
		Vid vid = new Vid();
		vid.setUin("");
		assertFalse(interceptor.onSave(vid, null, new Object[] { "" }, new String[] { "uin" }, null));
	}

	@Test
	@Ignore
	public void testOnSaveEncryptionFailed() throws IdRepoAppException {
		when(securityManager.encrypt(Mockito.any())).thenThrow(new IdRepoAppException());
		Vid vid = new Vid();
		vid.setUin("");
		try {
			interceptor.onSave(vid, null, new Object[] { "" }, new String[] { "uin" }, null);
		} catch (IdRepoAppUncheckedException e) {
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testOnLoad() throws IdRepoAppException {
		when(securityManager.decrypt(Mockito.any())).thenReturn("".getBytes());
		when(securityManager.hash(Mockito.any())).thenReturn("");
		Vid vid = new Vid();
		vid.setUin("");
		assertFalse(interceptor.onLoad(vid, null, new Object[] { "", "" }, new String[] { "uin", "uinHash" }, null));
	}

	@Test
	public void testOnLoadDecryptionFailed() throws IdRepoAppException {
		when(securityManager.decrypt(Mockito.any())).thenThrow(new IdRepoAppException());
		Vid vid = new Vid();
		vid.setUin("");
		try {
			interceptor.onLoad(vid, null, new Object[] { "", "" }, new String[] { "uin", "uinHash" }, null);
		} catch (IdRepoAppUncheckedException e) {
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testOnFlushDirty() throws IdRepoAppException {
		when(securityManager.encrypt(Mockito.any())).thenReturn("".getBytes());
		Vid vid = new Vid();
		vid.setUin("");
		assertFalse(interceptor.onFlushDirty(vid, null, new Object[] { "" }, null, new String[] { "uin" }, null));
	}

	@Test
	public void testOnFlushDirtyEncryptionFailed() throws IdRepoAppException {
		when(securityManager.encrypt(Mockito.any())).thenThrow(new IdRepoAppException());
		Vid vid = new Vid();
		vid.setUin("");
		try {
			interceptor.onFlushDirty(vid, null, new Object[] { "" }, null, new String[] { "uin" }, null);
		} catch (IdRepoAppUncheckedException e) {
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}
}
