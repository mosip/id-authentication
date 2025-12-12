package io.mosip.authentication.internal.service.dto;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AuthorizedRolesDtoTest {

	private AuthorizedRolesDto authorizedRolesDto;
	
	@Before
	public void before() {
		authorizedRolesDto = new AuthorizedRolesDto();
	}
	
	@Test
	public void testSetterAndGetter() {
		List<String> postAuthRoles = Arrays.asList("AUTH_PARTNER", "AUTH_ADMIN");
		List<String> postVerifyIdentityRoles = Arrays.asList("AUTH_PARTNER");
		List<String> getAuthTransactionsRoles = Arrays.asList("RESIDENT");
		List<String> postOtpRoles = Arrays.asList("RESIDENT");
		
		authorizedRolesDto.setPostauth(postAuthRoles);
		authorizedRolesDto.setPostverifyidentity(postVerifyIdentityRoles);
		authorizedRolesDto.setGetauthtransactionsindividualid(getAuthTransactionsRoles);
		authorizedRolesDto.setPostotp(postOtpRoles);
		
		assertEquals("Post auth roles should match", postAuthRoles, authorizedRolesDto.getPostauth());
		assertEquals("Post verify identity roles should match", postVerifyIdentityRoles, 
			authorizedRolesDto.getPostverifyidentity());
		assertEquals("Get auth transactions roles should match", getAuthTransactionsRoles, 
			authorizedRolesDto.getGetauthtransactionsindividualid());
		assertEquals("Post OTP roles should match", postOtpRoles, authorizedRolesDto.getPostotp());
	}
	
	@Test
	public void testWithNullValues() {
		authorizedRolesDto.setPostauth(null);
		authorizedRolesDto.setPostverifyidentity(null);
		authorizedRolesDto.setGetauthtransactionsindividualid(null);
		authorizedRolesDto.setPostotp(null);
		
		assertNull("Post auth should be null", authorizedRolesDto.getPostauth());
		assertNull("Post verify identity should be null", authorizedRolesDto.getPostverifyidentity());
		assertNull("Get auth transactions should be null", authorizedRolesDto.getGetauthtransactionsindividualid());
		assertNull("Post OTP should be null", authorizedRolesDto.getPostotp());
	}
	
	@Test
	public void testWithEmptyLists() {
		authorizedRolesDto.setPostauth(Arrays.asList());
		authorizedRolesDto.setPostverifyidentity(Arrays.asList());
		
		assertNotNull("Post auth should not be null", authorizedRolesDto.getPostauth());
		assertTrue("Post auth should be empty", authorizedRolesDto.getPostauth().isEmpty());
		assertNotNull("Post verify identity should not be null", authorizedRolesDto.getPostverifyidentity());
		assertTrue("Post verify identity should be empty", authorizedRolesDto.getPostverifyidentity().isEmpty());
	}
}
