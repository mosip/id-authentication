package io.mosip.registration.test.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.impl.UserDetailDAOImpl;
import io.mosip.registration.dto.UserDetailDto;
import io.mosip.registration.dto.UserDetailResponseDto;

public class UserDetailsDaoImpl {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	public UserDetailDAOImpl userDetailDAOImpl;

	@BeforeClass
	public static void init() {

		SessionContext.getInstance().userContext().setUserId("mosip");

	}

	@Test
	public void userDetlsDao() {
		UserDetailResponseDto userDetailsResponse = new UserDetailResponseDto();
		List<UserDetailDto> userDetails = new ArrayList<>();

		UserDetailDto user = new UserDetailDto();
		user.setUserName("110011");
		user.setUserPassword("test".getBytes());
		user.setRoles(Arrays.asList("SUPERADMIN"));
		user.setMobile("9894589435");
		user.setLangCode("eng");
		UserDetailDto user1 = new UserDetailDto();
		user1.setUserName("110011");
		user1.setUserPassword("test".getBytes());
		user1.setRoles(Arrays.asList("SUPERADMIN"));
		user1.setMobile("9894589435");
		user1.setLangCode("eng");
		userDetails.add(user);
		userDetails.add(user1);
		userDetailsResponse.setUserDetails(userDetails);
		userDetailDAOImpl.save(userDetailsResponse);
	}

}
