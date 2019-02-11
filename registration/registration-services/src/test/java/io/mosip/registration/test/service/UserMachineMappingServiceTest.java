package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.repositories.UserMachineMappingRepository;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.impl.UserMachineMappingServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
public class UserMachineMappingServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private UserMachineMappingServiceImpl userMachineMappingServiceImpl;
	@Mock
	private MachineMappingDAO machineMappingDAO;
	@Mock
	private UserMachineMappingRepository userMachineMappingRepository;
	@Mock
	private BaseService baseService;
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Test
	public void syncUserDetailsTest() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		List<UserMachineMapping> list = new ArrayList<>();
		UserMachineMapping userMachineMapping = new UserMachineMapping();
		UserDetail userDetail = new UserDetail();
		userDetail.setId("id");
		userDetail.setIsActive(true);
		userMachineMapping.setUserDetail(userDetail);
		list.add(userMachineMapping);
		String macAdress = "macAddress";
		String machineId = "machineId";
		String centerId = "centerId";
		Mockito.when(baseService.getMacAddress()).thenReturn(macAdress);
		Mockito.when(baseService.getStationId(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(baseService.getCenterId(Mockito.anyString())).thenReturn(centerId);
		Mockito.when(machineMappingDAO.getUserMappingDetails(Mockito.anyString())).thenReturn(list);
		Map<String, Object> map = new HashMap<>();

		map.put("cntrId", centerId);
		map.put("isActive", userMachineMapping.getUserDetail().getIsActive());
		map.put("machineId", machineId);
		map.put("userId", userMachineMapping.getUserDetail().getId());

		assertNotNull(userMachineMappingServiceImpl.syncUserDetails());
	}

	@Test
	public void syncUserDetailsOffLineTest() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		assertNotNull(userMachineMappingServiceImpl.syncUserDetails());

	}

}
