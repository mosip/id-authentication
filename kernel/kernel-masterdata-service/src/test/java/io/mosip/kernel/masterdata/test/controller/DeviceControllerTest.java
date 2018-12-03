

package io.mosip.kernel.masterdata.test.controller;

import static org.junit.Assert.assertTrue;
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
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.service.DeviceService;


@RunWith(MockitoJUnitRunner.class)
public class DeviceControllerTest {
	


	@InjectMocks
	private DeviceController deviceController;

	@Mock
	private DeviceService deviceService;
	
	@Mock
	private DeviceRepository deviceRepository;

	@Before
	public void setUp() {
		deviceController = new DeviceController();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getDeviceLangTest() {
		List<DeviceDto> deviceDtoList = new ArrayList<>();
		DeviceDto deviceDto = new DeviceDto();
		deviceDto.setCode("1001");
		deviceDto.setName("laptop");
		deviceDto.setSerialNum("1234567890");
		deviceDto.setIpAddress("100.100.100.80");
		deviceDto.setMacAddress("100.100.100.80");
		deviceDto.setDeviceSpecId("laptop_id");
		deviceDto.setLangCode("ENG");
		deviceDto.setIsActive(true);
		deviceDtoList.add(deviceDto);
		DeviceResponseDto deviceResponseDto = new DeviceResponseDto();
		deviceResponseDto.setDevices(deviceDtoList);
		Mockito.when(deviceService.getDeviceLangCode(Mockito.anyString())).thenReturn(deviceResponseDto);
		DeviceResponseDto actual = deviceController.getDeviceLang(Mockito.anyString());

		Assert.assertNotNull(actual);
		assertTrue(actual.getDevices().size() > 0);
	}


	
	@Test
	public void getDeviceLangCodeAndDeviceTypeTest() {
		DeviceLangCodeDtypeDto deviceLangCodeDtypeDto = new DeviceLangCodeDtypeDto();
		List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = new ArrayList<>();
		deviceLangCodeDtypeDto.setId("1001");
		deviceLangCodeDtypeDto.setName("Laptop");

		deviceLangCodeDtypeDtoList.add(deviceLangCodeDtypeDto);
		DeviceLangCodeResponseDto deviceLangCodeResponseDto = new DeviceLangCodeResponseDto();
		deviceLangCodeResponseDto.setDevices(deviceLangCodeDtypeDtoList);
		Mockito.when(deviceService.getDeviceLangCodeAndDeviceType(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceLangCodeResponseDto);
		DeviceLangCodeResponseDto actual = deviceController.getDeviceLangCodeAndDeviceType(Mockito.anyString(),
				Mockito.anyString());
		Assert.assertNotNull(actual);
		assertTrue(actual.getDevices().size() > 0);
	}

}