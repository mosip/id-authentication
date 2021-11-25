package io.mosip.authentication.internal.service.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.exception.RestServiceException;

/**
 * 
 * @author Loganathan Sekar
 *
 */
public class InternalAuthSecurityManagerTest {

	InternalAuthSecurityManager authSecurityManager = new InternalAuthSecurityManager();

	@Test
	@Ignore
	public void testGetUser() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);

		assertEquals("myuser", authSecurityManager.getUser());
	}

	@Test
	@Ignore
	public void testGetUserWrongPrincipal() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getPrincipal()).thenReturn(new Object());

		assertNull(authSecurityManager.getUser());
	}

	@Test
	@Ignore
	public void testGetUserNullPrincipal() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getPrincipal()).thenReturn(null);

		assertNull(authSecurityManager.getUser());
	}

	@Test
	@Ignore
	public void testGetUserNullAuthentication() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Mockito.when(context.getAuthentication()).thenReturn(null);

		assertNull(authSecurityManager.getUser());
	}

	@Test
	@Ignore
	public void testGetUserNullContext() throws IdAuthenticationBusinessException, RestServiceException {
		assertNull(authSecurityManager.getUser());
	}

}
