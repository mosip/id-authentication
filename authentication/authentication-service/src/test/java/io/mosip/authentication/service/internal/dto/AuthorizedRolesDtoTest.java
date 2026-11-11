package io.mosip.authentication.service.internal.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorizedRolesDtoTest {

	private AuthorizedRolesDto authorizedRolesDto;
	
	@BeforeEach
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
		
		assertEquals(postAuthRoles, authorizedRolesDto.getPostauth(), "Post auth roles should match");
		assertEquals( postVerifyIdentityRoles, 
			authorizedRolesDto.getPostverifyidentity(), "Post verify identity roles should match");
		assertEquals( getAuthTransactionsRoles, 
			authorizedRolesDto.getGetauthtransactionsindividualid(), "Get auth transactions roles should match");
		assertEquals(postOtpRoles, authorizedRolesDto.getPostotp(), "Post OTP roles should match");
	}
	
	@Test
	public void testWithNullValues() {
		authorizedRolesDto.setPostauth(null);
		authorizedRolesDto.setPostverifyidentity(null);
		authorizedRolesDto.setGetauthtransactionsindividualid(null);
		authorizedRolesDto.setPostotp(null);
		
		assertNull(authorizedRolesDto.getPostauth(), "Post auth should be null");
		assertNull(authorizedRolesDto.getPostverifyidentity(), "Post verify identity should be null");
		assertNull(authorizedRolesDto.getGetauthtransactionsindividualid(), "Get auth transactions should be null");
		assertNull(authorizedRolesDto.getPostotp(), "Post OTP should be null");
	}
	
	@Test
	public void testWithEmptyLists() {
		authorizedRolesDto.setPostauth(Arrays.asList());
		authorizedRolesDto.setPostverifyidentity(Arrays.asList());
		
		assertNotNull(authorizedRolesDto.getPostauth(), "Post auth should not be null");
		assertTrue(authorizedRolesDto.getPostauth().isEmpty(), "Post auth should be empty");
		assertNotNull(authorizedRolesDto.getPostverifyidentity(), "Post verify identity should not be null");
		assertTrue(authorizedRolesDto.getPostverifyidentity().isEmpty(), "Post verify identity should be empty");
	}
}
