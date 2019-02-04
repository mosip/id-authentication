package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.ScreenAuthorizationDetails;
import io.mosip.registration.dao.impl.ScreenAuthorizationDAOImpl;
import io.mosip.registration.repositories.ScreenAuthorizationRepository;

public class ScreenAuthorizationDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private ScreenAuthorizationDAOImpl screenAuthorizationDAOImpl;

	@Mock
	private ScreenAuthorizationRepository screenAuthorizationRepository;

	@Test
	public void getScreenAuthorizationDetailsTest() {

		Set<ScreenAuthorizationDetails> authorizationList = new HashSet<>();
		List<String> roleList = new ArrayList<>();
		Mockito.when(screenAuthorizationRepository
				.findByScreenAuthorizationIdRoleCodeInAndIsPermittedTrueAndIsActiveTrue(roleList))
				.thenReturn(authorizationList);
		//assertFalse(authorizationList.isEmpty());
		assertNotNull(screenAuthorizationDAOImpl.getScreenAuthorizationDetails(roleList));

	}

}
