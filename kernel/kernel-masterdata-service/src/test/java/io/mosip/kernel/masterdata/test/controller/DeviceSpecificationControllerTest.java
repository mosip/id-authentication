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

import io.mosip.kernel.masterdata.controller.DeviceSpecificationController;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationResponseDto;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;

@RunWith(MockitoJUnitRunner.class)
public class DeviceSpecificationControllerTest {

	@InjectMocks
	private DeviceSpecificationController deviceSpecificationController;

	@Mock
	private DeviceSpecificationService deviceSpecificationService;

	DeviceSpecificationDto deviceSpecificationDto;
	List<DeviceSpecificationDto> deviceSpecificationDtoList = new ArrayList<>();

	@Before
	public void setUp() {
		deviceSpecificationController = new DeviceSpecificationController();
		MockitoAnnotations.initMocks(this);

		deviceSpecificationDto = new DeviceSpecificationDto();
		deviceSpecificationDto.setId("100");
		deviceSpecificationDto.setName("Laptop");
		deviceSpecificationDto.setBrand("HP");
		deviceSpecificationDto.setLangCode("ENG");
		deviceSpecificationDto.setDeviceTypeCode("window_10");
		deviceSpecificationDto.setModel("pavalian_dv6");
		deviceSpecificationDto.setDescription("laptop Discription");

		deviceSpecificationDtoList.add(deviceSpecificationDto);
	}

	@Test
	public void testGetDeviceSpecificationByLanguageCode() {
		Mockito.when(deviceSpecificationService.findDeviceSpecificationByLangugeCode("ENG"))
				.thenReturn(deviceSpecificationDtoList);
		DeviceSpecificationResponseDto actual = deviceSpecificationController
				.getDeviceSpecificationByLanguageCode("ENG");

		Assert.assertNotNull(actual);
		assertTrue(actual.getDevicespecifications().size() > 0);
	}

	@Test
	public void testGetDeviceSpecificationByLanguageCodeAndDeviceTypeCode() {

		Mockito.when(deviceSpecificationService
				.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceSpecificationDtoList);
		DeviceSpecificationResponseDto actual = deviceSpecificationController
				.getDeviceSpecificationByLanguageCodeAndDeviceTypeCode("ENG", "operating_sys");

		Assert.assertNotNull(actual);
		assertTrue(actual.getDevicespecifications().size() > 0);
	}

}
