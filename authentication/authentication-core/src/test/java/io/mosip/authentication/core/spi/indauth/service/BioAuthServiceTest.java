package io.mosip.authentication.core.spi.indauth.service;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

public class BioAuthServiceTest {
	
	private AuthStatusInfo authStatusInfo;
	
	@Before
	public void init() {
		authStatusInfo = new AuthStatusInfo();
	}

	private BioAuthService createTestSubject() {
		return new BioAuthService() {

			@Override
			public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,
					Map<String, List<IdentityInfoDTO>> idInfo, String partnerId, boolean isAuth)
					throws IdAuthenticationBusinessException {
				// TODO Auto-generated method stub
				return authStatusInfo;
			}
			
		};
	}

	@Test
	public void testAuthenticate() throws Exception {
		BioAuthService testSubject;
		AuthRequestDTO authRequestDTO = null;
		String uin = "12345";
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String partnerId = "112233";
		AuthStatusInfo result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.authenticate(authRequestDTO, uin, idInfo, partnerId);

		assertEquals(authStatusInfo, result);
	}
}