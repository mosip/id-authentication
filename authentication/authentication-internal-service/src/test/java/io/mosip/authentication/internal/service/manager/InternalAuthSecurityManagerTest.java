package io.mosip.authentication.internal.service.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;

/**
 * 
 * @author Loganathan Sekar
 *
 */
public class InternalAuthSecurityManagerTest {

	InternalAuthSecurityManager authSecurityManager = new InternalAuthSecurityManager();

	@Test
	public void testGetUser() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		AuthUserDetails user = Mockito.mock(AuthUserDetails.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(user);
		Mockito.when(user.getUserId()).thenReturn("myuser");

		assertEquals("myuser", authSecurityManager.getUser());
	}

	@Test
	public void testGetUserWrongPrincipal() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		AuthUserDetails user = Mockito.mock(AuthUserDetails.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(new Object());
		Mockito.when(user.getUserId()).thenReturn("myuser");

		assertNull(authSecurityManager.getUser());
	}

	@Test
	public void testGetUserNullPrincipal() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		AuthUserDetails user = Mockito.mock(AuthUserDetails.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(null);
		Mockito.when(user.getUserId()).thenReturn("myuser");

		assertNull(authSecurityManager.getUser());
	}

	@Test
	public void testGetUserNullAuthentication() throws IdAuthenticationBusinessException, RestServiceException {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		Mockito.when(context.getAuthentication()).thenReturn(null);

		assertNull(authSecurityManager.getUser());
	}

	@Test
	public void testGetUserNullContext() throws IdAuthenticationBusinessException, RestServiceException {
		assertNull(authSecurityManager.getUser());
	}

}
