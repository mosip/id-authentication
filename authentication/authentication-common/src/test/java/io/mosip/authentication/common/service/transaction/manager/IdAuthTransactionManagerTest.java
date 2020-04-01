package io.mosip.authentication.common.service.transaction.manager;

import static org.junit.Assert.assertEquals;

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
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;

/**
 * 
 * @author Loganathan Sekar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, RestRequestFactory.class,
		ObjectMapper.class, RestRequestFactory.class })
public class IdAuthTransactionManagerTest {
	
	@InjectMocks
	IdAuthTransactionManager authTransactionManager;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private ObjectMapper mapper;
	
	/** The rest builder. */
	@Autowired
	private RestRequestFactory restBuilder;
	
	/** The rest helper. */
	@Mock
	private RestHelper restHelper;
	
	@Before
	public void init() {
		ReflectionTestUtils.setField(authTransactionManager, "mapper", mapper);
		ReflectionTestUtils.setField(authTransactionManager, "environment", environment);
		ReflectionTestUtils.setField(authTransactionManager, "restBuilder", restBuilder);
		ReflectionTestUtils.setField(authTransactionManager, "restHelper", restHelper);
		
	}
	
	@Test
	public void testEncryptWithSalt() throws IdAuthenticationBusinessException, RestServiceException {
		ObjectNode objectNode = Mockito.mock(ObjectNode.class);
		ObjectNode responseNode = Mockito.mock(ObjectNode.class);
		ObjectNode dataNode = Mockito.mock(ObjectNode.class);
		Mockito.when(objectNode.has("response")).thenReturn(true);
		Mockito.when(objectNode.get("response")).thenReturn(responseNode);
		Mockito.when(responseNode.has("data")).thenReturn(true);
		Mockito.when(responseNode.get("data")).thenReturn(dataNode);
		Mockito.when(dataNode.asText()).thenReturn("abcd");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(objectNode);
		byte[] encryptWithSalt = authTransactionManager.encryptWithSalt("Hello".getBytes(), "20190101".getBytes());
		assertEquals("abcd", new String(encryptWithSalt));
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testEncryptWithSaltFalseHasResponse() throws IdAuthenticationBusinessException, RestServiceException {
		ObjectNode objectNode = Mockito.mock(ObjectNode.class);
		ObjectNode responseNode = Mockito.mock(ObjectNode.class);
		ObjectNode dataNode = Mockito.mock(ObjectNode.class);
		Mockito.when(objectNode.has("response")).thenReturn(false);
		Mockito.when(objectNode.get("response")).thenReturn(responseNode);
		Mockito.when(responseNode.has("data")).thenReturn(true);
		Mockito.when(responseNode.get("data")).thenReturn(dataNode);
		Mockito.when(dataNode.asText()).thenReturn("abcd");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(objectNode);
		authTransactionManager.encryptWithSalt("Hello".getBytes(), "20190101".getBytes());
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testEncryptWithSaltNullResponse() throws IdAuthenticationBusinessException, RestServiceException {
		ObjectNode objectNode = Mockito.mock(ObjectNode.class);
		ObjectNode responseNode = Mockito.mock(ObjectNode.class);
		ObjectNode dataNode = Mockito.mock(ObjectNode.class);
		Mockito.when(objectNode.has("response")).thenReturn(true);
		Mockito.when(responseNode.has("data")).thenReturn(true);
		Mockito.when(responseNode.get("data")).thenReturn(dataNode);
		Mockito.when(dataNode.asText()).thenReturn("abcd");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(objectNode);
		authTransactionManager.encryptWithSalt("Hello".getBytes(), "20190101".getBytes());
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testEncryptWithSaltFalseHasData() throws IdAuthenticationBusinessException, RestServiceException {
		ObjectNode objectNode = Mockito.mock(ObjectNode.class);
		ObjectNode responseNode = Mockito.mock(ObjectNode.class);
		ObjectNode dataNode = Mockito.mock(ObjectNode.class);
		Mockito.when(objectNode.has("response")).thenReturn(true);
		Mockito.when(objectNode.get("response")).thenReturn(responseNode);
		Mockito.when(responseNode.has("data")).thenReturn(false);
		Mockito.when(responseNode.get("data")).thenReturn(dataNode);
		Mockito.when(dataNode.asText()).thenReturn("abcd");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(objectNode);
		authTransactionManager.encryptWithSalt("Hello".getBytes(), "20190101".getBytes());
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testEncryptWithSaltNullData() throws IdAuthenticationBusinessException, RestServiceException {
		ObjectNode objectNode = Mockito.mock(ObjectNode.class);
		ObjectNode responseNode = Mockito.mock(ObjectNode.class);
		ObjectNode dataNode = Mockito.mock(ObjectNode.class);
		Mockito.when(objectNode.has("response")).thenReturn(true);
		Mockito.when(objectNode.get("response")).thenReturn(responseNode);
		Mockito.when(responseNode.has("data")).thenReturn(true);
		Mockito.when(dataNode.asText()).thenReturn("abcd");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(objectNode);
		authTransactionManager.encryptWithSalt("Hello".getBytes(), "20190101".getBytes());
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testEncryptWithSaltWithRestServiceException() throws IdAuthenticationBusinessException, RestServiceException {
		ObjectNode objectNode = Mockito.mock(ObjectNode.class);
		ObjectNode responseNode = Mockito.mock(ObjectNode.class);
		ObjectNode dataNode = Mockito.mock(ObjectNode.class);
		Mockito.when(objectNode.has("response")).thenReturn(true);
		Mockito.when(objectNode.get("response")).thenReturn(responseNode);
		Mockito.when(responseNode.has("data")).thenReturn(true);
		Mockito.when(responseNode.get("data")).thenReturn(dataNode);
		Mockito.when(dataNode.asText()).thenReturn("abcd");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(IdAuthenticationErrorConstants.CLIENT_ERROR));
		authTransactionManager.encryptWithSalt("Hello".getBytes(), "20190101".getBytes());
	}
	

}
