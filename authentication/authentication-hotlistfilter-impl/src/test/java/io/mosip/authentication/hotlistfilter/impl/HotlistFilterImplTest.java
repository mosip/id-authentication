package io.mosip.authentication.hotlistfilter.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistIdTypes;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class HotlistFilterImplTest {
	
	@InjectMocks
	private DeviceHotlistFilterImpl deviceHotlistFilterImpl;
	
	@InjectMocks 
	private DeviceProviderHotlistFilterImpl deviceProviderHotlistFilterImpl;
	
	@InjectMocks
	private IndividualIdHotlistFilterImpl individualIdHotlistFilterImpl;
	
	@InjectMocks
	private PartnerIdHotlistFilterImpl partnerIdHotlistFilterImpl;
	
	
	@Mock
	private HotlistService hotlistService;

	@Before
	public void before() {
		HotlistDTO response = new HotlistDTO();
		response.setStatus(HotlistStatus.UNBLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(response);
	}

	@Test
	public void testIsIndividualIdHotlisted() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.BLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		
		try {
			ReflectionTestUtils.invokeMethod(individualIdHotlistFilterImpl, "isIndividualIdHotlisted", "", "");
		} catch (UndeclaredThrowableException e) {
			Throwable throwable = e.getUndeclaredThrowable();
			assertTrue(throwable instanceof IdAuthenticationFilterException);
			IdAuthenticationFilterException filterException = (IdAuthenticationFilterException) throwable;
			
			assertTrue(filterException.getErrorCode()
					.contentEquals(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode()));
			assertTrue(filterException.getErrorText().contentEquals(
					String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(), IdAuthCommonConstants.INDIVIDUAL_ID)));
			return;
		}
		fail();
		
	}

	@Test
	public void testIsIndividualIdHotlistedUnblocked() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.UNBLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		ReflectionTestUtils.invokeMethod(individualIdHotlistFilterImpl, "isIndividualIdHotlisted", "", "");
	}

	@Test
	public void testIsDevicesHotlisted() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.BLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		BioIdentityInfoDTO biometric = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		data.setDigitalId(digitalId);
		biometric.setData(data);
		try {
			ReflectionTestUtils.invokeMethod(deviceHotlistFilterImpl, "isDevicesHotlisted",
					Collections.singletonList(biometric));
		} catch (UndeclaredThrowableException e) {
			Throwable throwable = e.getUndeclaredThrowable();
			assertTrue(throwable instanceof IdAuthenticationFilterException);
			IdAuthenticationFilterException filterException = (IdAuthenticationFilterException) throwable;
			
			assertTrue(filterException.getErrorCode()
					.contentEquals(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode()));
			assertTrue(filterException.getErrorText()
					.contentEquals(String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
							String.format(IdAuthCommonConstants.BIO_PATH, "0", HotlistIdTypes.DEVICE))));
			return;
		}
		fail();
	}

	@Test
	public void testIsDevicesHotlistedUnblocked() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.UNBLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		BioIdentityInfoDTO biometric = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		biometric.setData(data);
		ReflectionTestUtils.invokeMethod(deviceHotlistFilterImpl, "isDevicesHotlisted",
				Collections.singletonList(biometric));
	}

	@Test
	public void testIsDeviceProviderHotlisted() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.BLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		BioIdentityInfoDTO biometric = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		biometric.setData(data);
		
		try {
			ReflectionTestUtils.invokeMethod(deviceProviderHotlistFilterImpl, "isDeviceProviderHotlisted",
				Collections.singletonList(biometric));
		} catch (UndeclaredThrowableException e) {
			Throwable throwable = e.getUndeclaredThrowable();
			assertTrue(throwable instanceof IdAuthenticationFilterException);
			IdAuthenticationFilterException filterException = (IdAuthenticationFilterException) throwable;
			
			assertTrue(filterException.getErrorCode()
					.contentEquals(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode()));
			assertTrue(filterException.getErrorText()
					.contentEquals(String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
							String.format(IdAuthCommonConstants.BIO_PATH, "0", HotlistIdTypes.DEVICE_PROVIDER))));
			return;
		}
		fail();
		
	}

	@Test
	public void testIsDeviceProviderHotlistedUnblocked() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.UNBLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		BioIdentityInfoDTO biometric = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		DigitalId digitalId = new DigitalId();
		digitalId.setSerialNo("");
		digitalId.setMake("");
		digitalId.setModel("");
		digitalId.setDeviceProvider("");
		digitalId.setDeviceProviderId("");
		data.setDigitalId(digitalId);
		biometric.setData(data);
		ReflectionTestUtils.invokeMethod(deviceProviderHotlistFilterImpl, "isDeviceProviderHotlisted",
				Collections.singletonList(biometric));
	}

	@Test
	public void testIsPartnerIdHotlisted() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.BLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		try {
			ReflectionTestUtils.invokeMethod(partnerIdHotlistFilterImpl, "isPartnerIdHotlisted", Optional.of(""));
		} catch (UndeclaredThrowableException e) {
			Throwable throwable = e.getUndeclaredThrowable();
			assertTrue(throwable instanceof IdAuthenticationFilterException);
			IdAuthenticationFilterException filterException = (IdAuthenticationFilterException) throwable;
			
			assertTrue(filterException.getErrorCode()
					.contentEquals(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode()));
			assertTrue(filterException.getErrorText().contentEquals(String
					.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(), "partnerId")));
			return;
		}
		fail();
		
	}

	@Test
	public void testIsPartnerIdHotlistedUnblocked() {
		HotlistDTO result = new HotlistDTO();
		result.setStatus(HotlistStatus.UNBLOCKED);
		when(hotlistService.getHotlistStatus(Mockito.any(), Mockito.any())).thenReturn(result);
		ReflectionTestUtils.invokeMethod(partnerIdHotlistFilterImpl, "isPartnerIdHotlisted", Optional.of(""));
	}

}
