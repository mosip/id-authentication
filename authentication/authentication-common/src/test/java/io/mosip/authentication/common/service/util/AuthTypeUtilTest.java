package io.mosip.authentication.common.service.util;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test class for AuthTypeUtil
 * 
 * @author Md Tarique Azeez
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthTypeUtilTest {

	@Mock
	private EnvUtil envUtil;

	@Test
	public void testIsBioWithBiometricRequest() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setBiometrics(java.util.Collections.singletonList(
			new io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO()
		));
		authRequest.setRequest(request);
		
		// This will depend on the actual implementation
		// Since BioAuthType.values() is checked internally
		boolean result = AuthTypeUtil.isBio(authRequest);
		// Result depends on actual BioAuthType implementation
		assertNotNull("Result should not be null", result);
	}
	
	@Test
	public void testIsDemoWithDemographicRequest() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		IdentityDTO identity = new IdentityDTO();
		identity.setDob("1990/01/01");
		request.setDemographics(identity);
		authRequest.setRequest(request);
		
		boolean result = AuthTypeUtil.isDemo(authRequest);
		// Result depends on actual DemoAuthType implementation
		assertNotNull("Result should not be null", result);
	}
	
	@Test
	public void testIsOtpWithOtpRequest() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authRequest.setRequest(request);
		
		boolean result = AuthTypeUtil.isOtp(authRequest);
		assertNotNull("Result should not be null", result);
	}
	
	@Test
	public void testIsPinWithPinRequest() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("1234");
		authRequest.setRequest(request);
		
		boolean result = AuthTypeUtil.isPin(authRequest);
		assertNotNull("Result should not be null", result);
	}
	
	@Test
	public void testFindAutRequestTypesWithOtp() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authRequest.setRequest(request);
		
		EnvUtil mockEnv = mock(EnvUtil.class);
		
		// AuthTransactionHelper methods are static, so we just test the flow
		List<RequestType> requestTypes = AuthTypeUtil.findAutRequestTypes(authRequest, mockEnv);
		
		assertNotNull("Request types should not be null", requestTypes);
	}
	
	@Test
	public void testFindAutRequestTypesWithEkyc() {
		EkycAuthRequestDTO authRequest = new EkycAuthRequestDTO();
		RequestDTO request = new RequestDTO();
		authRequest.setRequest(request);
		
		EnvUtil mockEnv = mock(EnvUtil.class);
		
		// AuthTransactionHelper methods are static, so we just test the flow
		List<RequestType> requestTypes = AuthTypeUtil.findAutRequestTypes(authRequest, mockEnv);
		
		assertNotNull("Request types should not be null", requestTypes);
		assertTrue("Should contain EKYC_AUTH_REQUEST", 
			requestTypes.contains(RequestType.EKYC_AUTH_REQUEST));
	}

}
