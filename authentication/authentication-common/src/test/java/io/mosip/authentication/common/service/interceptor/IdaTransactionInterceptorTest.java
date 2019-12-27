package io.mosip.authentication.common.service.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import io.mosip.authentication.common.service.transaction.manager.IdAuthTransactionManager;
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
	private IdAuthTransactionManager idAuthTransactionManager;

	@InjectMocks
	IdaTransactionInterceptor idaTransactionInterceptor;
	
	@Before
	public void init() {
		ReflectionTestUtils.setField(idaTransactionInterceptor, "idAuthTransactionManager", idAuthTransactionManager);
		
	}

	
	@Test
	public void testOnSave() throws IdAuthenticationBusinessException, RestServiceException {
		AutnTxn entity = new AutnTxn();
		String splitter = IdRepoConstants.SPLITTER;
		entity.setUin("123" + splitter + "456" + splitter + "789");
		Mockito.when(idAuthTransactionManager.encryptWithSalt(Mockito.any(), Mockito.any())).thenReturn("abc".getBytes());
		idaTransactionInterceptor.onSave(entity, null, new String[] {"123"}, new String[] {"uin"}, null);
		assertEquals("123_abc", entity.getUin());
	}
	
	@Test
	public void testOnSave_nullEntity() throws IdAuthenticationBusinessException, RestServiceException {
		AutnTxn entity =  new AutnTxn();;
		entity.setUin(null);
		Mockito.when(idAuthTransactionManager.encryptWithSalt(Mockito.any(), Mockito.any())).thenReturn("123".getBytes());
		idaTransactionInterceptor.onSave(entity, null, new String[] {"123"}, new String[] {"uin"}, null);
		assertNull(entity.getUin());
	}

}
