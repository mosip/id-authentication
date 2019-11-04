package io.mosip.registration.test.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.RegisteredDeviceDAOImpl;
import io.mosip.registration.entity.RegisteredDeviceMaster;
import io.mosip.registration.repositories.RegisteredDeviceRepository;

public class RegisteredDeviceDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegisteredDeviceDAOImpl registeredDeviceDAOImpl;
	
	@Mock
	private RegisteredDeviceRepository registeredDeviceRepository;

	@Test
	public void getRegisteredDevicesTest() {
		
		RegisteredDeviceMaster registerdDeviceMaster = new RegisteredDeviceMaster();

		registerdDeviceMaster.setIsActive(true);
		registerdDeviceMaster.setCrBy("createdBy");
		List<RegisteredDeviceMaster> list = new ArrayList<>();
		list.add(registerdDeviceMaster);
		
		Mockito.when(registeredDeviceRepository.findAll()).thenReturn(list);
		
		assertEquals(list, registeredDeviceDAOImpl.getRegisteredDevices());
	}
	
	@Test
	public void getRegisteredDevicesTestWithDeviceCode() {
		
		RegisteredDeviceMaster registerdDeviceMaster = new RegisteredDeviceMaster();

		registerdDeviceMaster.setIsActive(true);
		registerdDeviceMaster.setCrBy("createdBy");
		List<RegisteredDeviceMaster> list = new ArrayList<>();
		list.add(registerdDeviceMaster);
		
		Mockito.when(registeredDeviceRepository.findAllByIsActiveTrueAndDeviceId(Mockito.anyString())).thenReturn(list);
		
		assertEquals(list, registeredDeviceDAOImpl.getRegisteredDevices(Mockito.anyString()));
	}

}
