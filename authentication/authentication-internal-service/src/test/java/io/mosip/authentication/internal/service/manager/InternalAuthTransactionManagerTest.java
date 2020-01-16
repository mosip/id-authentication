package io.mosip.authentication.internal.service.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;

/**
 * 
 * @author Loganathan Sekar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, RestRequestFactory.class,
		ObjectMapper.class, RestRequestFactory.class, RestHelperImpl.class })
public class InternalAuthTransactionManagerTest {
	
	@InjectMocks
	InternalAuthTransactionManager authTransactionManager;
	
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
	public void testGetUser() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		AuthUserDetails user= Mockito.mock(AuthUserDetails.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(user);
		Mockito.when(user.getUserId()).thenReturn("myuser");
		
		assertEquals("myuser",authTransactionManager.getUser());
	}
	
	@Test
	public void testGetUserWrongPrincipal() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		AuthUserDetails user= Mockito.mock(AuthUserDetails.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(new Object());
		Mockito.when(user.getUserId()).thenReturn("myuser");
		
		assertNull(authTransactionManager.getUser());
	}
	
	@Test
	public void testGetUserNullPrincipal() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		AuthUserDetails user= Mockito.mock(AuthUserDetails.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(null);
		Mockito.when(user.getUserId()).thenReturn("myuser");
		
		assertNull(authTransactionManager.getUser());
	}
	
	@Test
	public void testGetUserNullAuthentication() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Mockito.when(context.getAuthentication()).thenReturn(null);
		
		assertNull(authTransactionManager.getUser());
	}
	
	@Test
	public void testGetUserNullContext() throws IdAuthenticationBusinessException, RestServiceException {
		assertNull(authTransactionManager.getUser());
	}
	
	

}
