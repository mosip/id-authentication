package io.mosip.idrepository.vid.test.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.helper.RestHelper;
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
	
	@Mock
	RestHelper restHelper;

	@Mock
	RestRequestBuilder restBuilder;
	
	@InjectMocks
	ObjectMapper mapper;
	
	@Autowired
	Environment env;
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(securityManager, "env", env);
		ReflectionTestUtils.setField(securityManager, "mapper", mapper);
		ReflectionTestUtils.setField(interceptor, "securityManager", securityManager);
	}

	@Test
	public void testOnSave() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(securityManager.encryptWithSalt(Mockito.any(),Mockito.any())).thenReturn("".getBytes());
		Vid vid = new Vid();
		vid.setUin("461_7329815461_7C9JlRD32RnFTzAmeTfIzg");
		assertFalse(interceptor.onSave(vid, null, new Object[] { "461_7329815461_7C9JlRD32RnFTzAmeTfIzg" }, new String[] { "uin" }, null));
	}

	@Test
	public void testOnSaveEncryptionFailed() throws IdRepoAppException {
		when(securityManager.encryptWithSalt(Mockito.any(),Mockito.any())).thenThrow(new IdRepoAppException());
		Vid vid = new Vid();
		vid.setUin("461_7329815461_7C9JlRD32RnFTzAmeTfIzg");
		try {
			interceptor.onSave(vid, null, new Object[] { "461_7329815461_7C9JlRD32RnFTzAmeTfIzg" }, new String[] { "uin" }, null);
		} catch (IdRepoAppUncheckedException e) {
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	@Ignore
	public void testOnLoad() throws IdRepoAppException {
		when(securityManager.decrypt(Mockito.any())).thenReturn("".getBytes());
		when(securityManager.hash(Mockito.any())).thenReturn("");
		Vid vid = new Vid();
		vid.setUin("");
		assertFalse(interceptor.onLoad(vid, null, new Object[] { "", "" }, new String[] { "uin", "uinHash" }, null));
	}

	@Test
	@Ignore
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
		when(securityManager.encryptWithSalt(Mockito.any(),Mockito.any())).thenReturn("".getBytes());
		Vid vid = new Vid();
		vid.setUin("461_7329815461_7C9JlRD32RnFTzAmeTfIzg");
		assertFalse(interceptor.onFlushDirty(vid, null, new Object[] { "461_7329815461_7C9JlRD32RnFTzAmeTfIzg" }, null, new String[] { "uin" }, null));
	}

	@Test
	public void testOnFlushDirtyEncryptionFailed() throws IdRepoAppException {
		when(securityManager.encryptWithSalt(Mockito.any(),Mockito.any())).thenThrow(new IdRepoAppException());
		Vid vid = new Vid();
		vid.setUin("461_7329815461_7C9JlRD32RnFTzAmeTfIzg");
		try {
			interceptor.onFlushDirty(vid, null, new Object[] { "461_7329815461_7C9JlRD32RnFTzAmeTfIzg" }, null, new String[] { "uin" }, null);
		} catch (IdRepoAppUncheckedException e) {
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage(), e.getErrorText());
		}
	}
}
