package io.mosip.registration.test.service;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.service.BaseService;

public class BaseServiceTest {

	@Mock
	MachineMappingDAO machineMappingDAO;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	BaseService baseService;

	@Test
	public void isValidDeviceTest() {
		Mockito.when(machineMappingDAO.isValidDevice(DeviceTypes.FINGERPRINT, "SF0001")).thenReturn(false);
		Boolean test = baseService.isValidDevice(DeviceTypes.FINGERPRINT, "SF001");
		Assert.assertSame(false, test);
	}

}
