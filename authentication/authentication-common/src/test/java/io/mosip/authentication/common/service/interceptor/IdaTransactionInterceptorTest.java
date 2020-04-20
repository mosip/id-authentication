package io.mosip.authentication.common.service.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.idrepository.core.constant.IdRepoConstants;

/**
 * 
 * @author Loganathan Sekar
 *
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdaTransactionInterceptorTest {
	
	@Mock
	private IdAuthSecurityManager idAuthSecurityManager;

	@InjectMocks
	IdaTransactionInterceptor idaTransactionInterceptor;
	
	@Before
	public void init() {
		ReflectionTestUtils.setField(idaTransactionInterceptor, "idAuthSecurityManager", idAuthSecurityManager);
		
	}

	
	@Test
	public void testOnSave() throws IdAuthenticationBusinessException, RestServiceException {
		AutnTxn entity = new AutnTxn();
		String splitter = IdRepoConstants.SPLITTER;
		entity.setUin("123" + splitter + "456" + splitter + "789");
		idaTransactionInterceptor.onSave(entity, null, new String[] {"123"}, new String[] {"uin"}, null);
		assertEquals("123_null", entity.getUin());
	}
	
	@Test
	public void testOnSave_nullEntity() throws IdAuthenticationBusinessException, RestServiceException {
		AutnTxn entity =  new AutnTxn();
		entity.setUin(null);
		idaTransactionInterceptor.onSave(entity, null, new String[] {"123"}, new String[] {"uin"}, null);
		assertNull(entity.getUin());
	}
	
	@Test(expected=IdAuthUncheckedException.class)
	public void testOnSaveWithException() throws IdAuthenticationBusinessException, RestServiceException {
		when(idAuthSecurityManager.encrypt(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT));
		AutnTxn entity =  new AutnTxn();
		String splitter = IdRepoConstants.SPLITTER;
		entity.setUin("123" + splitter + "456" + splitter + "789");
		idaTransactionInterceptor.onSave(entity, null, new String[] {"123"}, new String[] {"uin"}, null);
		assertNull(entity.getUin());
	}

}
