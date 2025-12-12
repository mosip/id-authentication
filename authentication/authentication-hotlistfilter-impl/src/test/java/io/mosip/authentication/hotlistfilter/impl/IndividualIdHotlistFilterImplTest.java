package io.mosip.authentication.hotlistfilter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;

@RunWith(MockitoJUnitRunner.class)
public class IndividualIdHotlistFilterImplTest {

	@Mock
	private HotlistService hotlistService;
	
	@InjectMocks
	private IndividualIdHotlistFilterImpl individualIdHotlistFilterImpl;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(individualIdHotlistFilterImpl, "hotlistService", hotlistService);
	}
	
	@Test
	public void testInit() throws IdAuthenticationFilterException {
		individualIdHotlistFilterImpl.init();
		// No exception should be thrown
	}
	
	@Test
	public void testValidateWithNonHotlistedId() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setIndividualId("1234567890");
		authRequest.setIndividualIdType("UIN");
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		HotlistDTO hotlistDTO = new HotlistDTO();
		hotlistDTO.setStatus(HotlistStatus.UNBLOCKED);
		
		String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText("1234567890".getBytes());
		Mockito.when(hotlistService.getHotlistStatus(hash, IdAuthCommonConstants.INDIVIDUAL_ID))
			.thenReturn(hotlistDTO);
		
		individualIdHotlistFilterImpl.validate(authRequest, identityData, properties);
	}
	
	@Test(expected = IdAuthenticationFilterException.class)
	public void testValidateWithHotlistedId() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setIndividualId("1234567890");
		authRequest.setIndividualIdType("UIN");
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		HotlistDTO hotlistDTO = new HotlistDTO();
		hotlistDTO.setStatus(HotlistStatus.BLOCKED);
		
		String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText("1234567890".getBytes());
		Mockito.when(hotlistService.getHotlistStatus(hash, IdAuthCommonConstants.INDIVIDUAL_ID))
			.thenReturn(hotlistDTO);
		
		individualIdHotlistFilterImpl.validate(authRequest, identityData, properties);
	}
	
	@Test
	public void testValidateWithNullIndividualId() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setIndividualId(null);
		authRequest.setIndividualIdType("UIN");
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		individualIdHotlistFilterImpl.validate(authRequest, identityData, properties);
		// Should not throw exception when individualId is null
	}
	
	@Test
	public void testValidateWithNullIndividualIdType() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setIndividualId("1234567890");
		authRequest.setIndividualIdType(null);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		individualIdHotlistFilterImpl.validate(authRequest, identityData, properties);
		// Should not throw exception when individualIdType is null
	}
	
	@Test
	public void testValidateWithVID() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setIndividualId("9876543210");
		authRequest.setIndividualIdType("VID");
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		HotlistDTO hotlistDTO = new HotlistDTO();
		hotlistDTO.setStatus(HotlistStatus.UNBLOCKED);
		
		String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText("9876543210".getBytes());
		Mockito.when(hotlistService.getHotlistStatus(hash, IdAuthCommonConstants.INDIVIDUAL_ID))
			.thenReturn(hotlistDTO);
		
		individualIdHotlistFilterImpl.validate(authRequest, identityData, properties);
	}
}
