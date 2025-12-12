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
import io.mosip.kernel.core.hotlist.constant.HotlistIdTypes;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;

@RunWith(MockitoJUnitRunner.class)
public class PartnerIdHotlistFilterImplTest {

	@Mock
	private HotlistService hotlistService;
	
	@InjectMocks
	private PartnerIdHotlistFilterImpl partnerIdHotlistFilterImpl;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(partnerIdHotlistFilterImpl, "hotlistService", hotlistService);
	}
	
	@Test
	public void testInit() throws IdAuthenticationFilterException {
		partnerIdHotlistFilterImpl.init();
		// No exception should be thrown
	}
	
	@Test
	public void testValidateWithNonHotlistedPartnerId() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		Map<String, Object> metadata = new HashMap<>();
		metadata.put(IdAuthCommonConstants.PARTNER_ID, "PARTNER123");
		authRequest.setMetadata(metadata);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		HotlistDTO hotlistDTO = new HotlistDTO();
		hotlistDTO.setStatus(HotlistStatus.UNBLOCKED);
		
		String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText("PARTNER123".getBytes());
		Mockito.when(hotlistService.getHotlistStatus(hash, HotlistIdTypes.PARTNER_ID))
			.thenReturn(hotlistDTO);
		
		partnerIdHotlistFilterImpl.validate(authRequest, identityData, properties);
	}
	
	@Test(expected = IdAuthenticationFilterException.class)
	public void testValidateWithHotlistedPartnerId() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		Map<String, Object> metadata = new HashMap<>();
		metadata.put(IdAuthCommonConstants.PARTNER_ID, "PARTNER123");
		authRequest.setMetadata(metadata);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		HotlistDTO hotlistDTO = new HotlistDTO();
		hotlistDTO.setStatus(HotlistStatus.BLOCKED);
		
		String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText("PARTNER123".getBytes());
		Mockito.when(hotlistService.getHotlistStatus(hash, HotlistIdTypes.PARTNER_ID))
			.thenReturn(hotlistDTO);
		
		partnerIdHotlistFilterImpl.validate(authRequest, identityData, properties);
	}
	
	@Test
	public void testValidateWithNullMetadata() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		authRequest.setMetadata(null);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		partnerIdHotlistFilterImpl.validate(authRequest, identityData, properties);
		// Should not throw exception when metadata is null
	}
	
	@Test
	public void testValidateWithMissingPartnerId() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		Map<String, Object> metadata = new HashMap<>();
		authRequest.setMetadata(metadata);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		partnerIdHotlistFilterImpl.validate(authRequest, identityData, properties);
		// Should not throw exception when partner ID is not in metadata
	}
}
