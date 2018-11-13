
package io.mosip.kernel.masterdata.test.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;



import io.mosip.kernel.masterdata.controller.DeviceController;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.DeviceResponseDto;
import io.mosip.kernel.masterdata.service.DeviceService;

@RunWith(MockitoJUnitRunner.class)
public class DeviceControllerTest {

	@InjectMocks
	private DeviceController deviceController;

	@Mock
	private DeviceService deviceService;

	@Before
	public void setUp() {
		deviceController = new DeviceController();
		MockitoAnnotations.initMocks(this);
	}



	/*@Test
	public void testGetDeviceLang() {
		List<DeviceDto> deviceDtoList = new ArrayList<>();
		DeviceDto  deviceDto = new DeviceDto();
		deviceDto.setId("1001");
		deviceDto.setName("laptop");
		deviceDto.setSerialNum("1234567890");
		deviceDto.setIpAddress("100.100.100.80");
		deviceDto.setMacAddress("100.100.100.80");
		deviceDto.setDspecId("laptop_id");
		deviceDto.setLangCode("ENG");
		deviceDto.setActive(true);
		deviceDtoList.add(deviceDto);
		Mockito.when(deviceService.getDeviceLangCode(Mockito.anyString())).thenReturn(deviceDtoList);
		DeviceResponseDto actual = deviceController.getDeviceLang(Mockito.anyString());

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.size() > 0);
	}*/
}

