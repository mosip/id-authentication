package io.mosip.authentication.hotlistfilter.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;

@RunWith(MockitoJUnitRunner.class)
public class DeviceHotlistFilterImplTest {

	@Mock
	private HotlistService hotlistService;
	
	@InjectMocks
	private DeviceHotlistFilterImpl deviceHotlistFilterImpl;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(deviceHotlistFilterImpl, "hotlistService", hotlistService);
	}
	
	@Test
	public void testInit() throws IdAuthenticationFilterException {
		deviceHotlistFilterImpl.init();
		// No exception should be thrown
	}
	
	@Test
	public void testValidateWithNonHotlistedDevice() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = createAuthRequestWithBiometric();
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("SN123");
		digitalId.setMake("Make1");
		digitalId.setModel("Model1");
		
		HotlistDTO hotlistDTO = new HotlistDTO();
		hotlistDTO.setStatus(HotlistStatus.UNBLOCKED);
		
		String deviceInfo = "SN123" + "Make1" + "Model1";
		String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText(deviceInfo.getBytes());
		
		deviceHotlistFilterImpl.validate(authRequest, identityData, properties);
	}
	
	@Test
	public void testValidateWithNoBiometrics() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		authRequest.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		deviceHotlistFilterImpl.validate(authRequest, identityData, properties);
		// Should not throw exception when no biometrics present
	}
	
	@Test
	public void testValidateWithEmptyBiometrics() throws IdAuthenticationFilterException {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setBiometrics(new ArrayList<>());
		authRequest.setRequest(request);
		
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		
		deviceHotlistFilterImpl.validate(authRequest, identityData, properties);
		// Should not throw exception when biometrics list is empty
	}
	
	private AuthRequestDTO createAuthRequestWithBiometric() {
		AuthRequestDTO authRequest = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		
		BioIdentityInfoDTO bioInfo = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("SN123");
		digitalId.setMake("Make1");
		digitalId.setModel("Model1");
		data.setDigitalId(digitalId);
		bioInfo.setData(data);
		
		List<BioIdentityInfoDTO> biometrics = new ArrayList<>();
		biometrics.add(bioInfo);
		request.setBiometrics(biometrics);
		authRequest.setRequest(request);
		
		return authRequest;
	}
}
