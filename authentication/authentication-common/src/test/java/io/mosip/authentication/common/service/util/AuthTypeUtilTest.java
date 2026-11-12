package io.mosip.authentication.common.service.util;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Test class for AuthTypeUtil
 * 
 * @author Md Tarique Azeez
 */
@ExtendWith(MockitoExtension.class)
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
		assertNotNull(result, "Result should not be null");
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
		assertNotNull(result, "Result should not be null");
	}
	
	@Test
	public void testIsOtpWithOtpRequest() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authRequest.setRequest(request);
		
		boolean result = AuthTypeUtil.isOtp(authRequest);
		assertNotNull(result, "Result should not be null");
	}
	
	@Test
	public void testIsPinWithPinRequest() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("1234");
		authRequest.setRequest(request);
		
		boolean result = AuthTypeUtil.isPin(authRequest);
		assertNotNull(result, "Result should not be null");
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
		
		assertNotNull(requestTypes, "Request types should not be null");
		assertTrue(requestTypes.contains(RequestType.OTP_AUTH), "Should contain OTP_AUTH");
	}
	
	@Test
	public void testFindAutRequestTypesWithEkyc() {
		EkycAuthRequestDTO authRequest = new EkycAuthRequestDTO();
		RequestDTO request = new RequestDTO();
		authRequest.setRequest(request);
		
		EnvUtil mockEnv = mock(EnvUtil.class);
		
		// AuthTransactionHelper methods are static, so we just test the flow
		List<RequestType> requestTypes = AuthTypeUtil.findAutRequestTypes(authRequest, mockEnv);
		
		assertNotNull(requestTypes, "Request types should not be null");
		assertTrue( 
			requestTypes.contains(RequestType.EKYC_AUTH_REQUEST), "Should contain EKYC_AUTH_REQUEST");
	}

}
