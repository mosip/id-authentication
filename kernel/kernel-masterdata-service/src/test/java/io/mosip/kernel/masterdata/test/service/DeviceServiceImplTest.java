package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataRetrievalFailureException;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.service.impl.DeviceServiceImpl;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class DeviceServiceImplTest {

	@InjectMocks
	DeviceServiceImpl deviceServiceImpl;

	@Mock
	private DeviceRepository deviceRepository;

	@Mock
	private MapperUtils objectMapperUtil;

	@Before
	public void setUp() {
		deviceServiceImpl = new DeviceServiceImpl();
		MockitoAnnotations.initMocks(this);
	}

	List<Device> deviceList = new ArrayList<>();
	List<DeviceDto> deviceDtoList = new ArrayList<>();

	@Test
	public void getDeviceLangCodeTest() {

		Device device = new Device();
		device.setId("1001");
		device.setName("Laptop");
		device.setLangCode("ENG");
		List<Device> deviceList = new ArrayList<>();
		deviceList.add(device);

		DeviceDto deviceDto = new DeviceDto();
		deviceDto.setId("1001");
		deviceDto.setName("Laptop");
		deviceDto.setName("ENG");
		List<DeviceDto> deviceDtoList = new ArrayList<>();
		deviceDtoList.add(deviceDto);
		Mockito.when(deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(deviceList);
		Mockito.when(objectMapperUtil.mapAll(deviceList, DeviceDto.class)).thenReturn(deviceDtoList);
		DeviceResponseDto actual = deviceServiceImpl.getDeviceLangCode("ENG");
		assertNotNull(actual);
		assertTrue(actual.getDevices().size() > 0);
	}

	@Test(expected = DataNotFoundException.class)
	public void getDeviceLangCodeThrowsDeviceNotFoundExceptionTest() {
		doReturn(null).when(deviceRepository).findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString());
		deviceServiceImpl.getDeviceLangCode("ENG");

	}

	@Test(expected = MasterDataServiceException.class)
	public void getMachineDetailAllThrowsDataAccessExcetionTest() {
		Mockito.when(deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		deviceServiceImpl.getDeviceLangCode("ENG");

	}

	@Test
	public void getDeviceLangCodeAndDeviceTypeTest() {

		Object[] objects = { "1001", "Laptop" };
		List<Object[]> objectList = new ArrayList<>();
		objectList.add(objects);

		DeviceLangCodeDtypeDto deviceLangCodeDtypeDto = new DeviceLangCodeDtypeDto();
		deviceLangCodeDtypeDto.setId("1001");
		deviceLangCodeDtypeDto.setName("Laptop");
		List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = new ArrayList<>();
		deviceLangCodeDtypeDtoList.add(deviceLangCodeDtypeDto);
		Mockito.when(deviceRepository.findByLangCodeAndDtypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(objectList);

		Mockito.when(objectMapperUtil.mapDeviceDto(Mockito.anyList())).thenReturn(deviceLangCodeDtypeDtoList);
		DeviceLangCodeResponseDto actual = deviceServiceImpl.getDeviceLangCodeAndDeviceType("ENG", "laptop_code");
		assertNotNull(actual);
		assertTrue(actual.getDevices().size() > 0);
	}

	@Test(expected = DataNotFoundException.class)
	public void getDeviceLangCodeAndDeviceTypeDeviceNotFoundExceptionTest() {
		doReturn(null).when(deviceRepository).findByLangCodeAndDtypeCode("ENG", "laptop_code");
		deviceServiceImpl.getDeviceLangCodeAndDeviceType("ENG", "laptop_code");

	}

	@Test(expected = MasterDataServiceException.class)
	public void getDeviceLangCodeAndDeviceTypeThrowsDataAccessExceptionTest() {
		Mockito.when(deviceRepository.findByLangCodeAndDtypeCode(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		deviceServiceImpl.getDeviceLangCodeAndDeviceType("ENG", "laptop_code");

	}

}
