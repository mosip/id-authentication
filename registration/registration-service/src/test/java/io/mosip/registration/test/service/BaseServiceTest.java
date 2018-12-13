package io.mosip.registration.test.service;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

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

		Boolean test = null;
		ReflectionTestUtils.setField(baseService, "fingerprintProviderName", "Mantra");
		Mockito.when(machineMappingDAO.isValidDevice("Fingerprint", "mantra", new Timestamp(new Date().getTime())))
				.thenReturn(false);
		test = baseService.isValidDevice("Fingerprint", "Mantra", "SF001");
		Assert.assertSame(false, test);
	}
}
