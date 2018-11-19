package io.mosip.kernel.masterdata.test.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceSpecificationServiceTest {
	@MockBean
	DeviceSpecificationRepository deviceSpecificationRepository;

	@Autowired
	DeviceSpecificationService deviceSpecificationService;

	List<DeviceSpecification> deviceSpecifications = null;
	List<DeviceSpecification> deviceSpecificationListWithDeviceTypeCode = null;

	@Before
	public void Setup() {

		deviceSpecifications = new ArrayList<>();
		DeviceSpecification deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("lp");
		deviceSpecification.setName("laptop");
		deviceSpecification.setBrand("hp");
		deviceSpecification.setModel("pavalian_dv6");
		deviceSpecification.setDeviceTypeCode("operating_sys");
		deviceSpecification.setMinDriverversion("window_10");
		deviceSpecification.setDescription("laptop discription");
		deviceSpecification.setLangCode("ENG");
		deviceSpecification.setIsActive(true);
		deviceSpecifications.add(deviceSpecification);
		DeviceSpecification deviceSpecification1 = new DeviceSpecification();
		deviceSpecification1.setId("printer");
		deviceSpecification1.setName("printer");
		deviceSpecification1.setBrand("hp");
		deviceSpecification1.setModel("marker_dv6");
		deviceSpecification1.setDeviceTypeCode("printer_id");
		deviceSpecification1.setMinDriverversion("ver_5.0");
		deviceSpecification1.setDescription("printer discription");
		deviceSpecification1.setLangCode("ENG");
		deviceSpecification1.setIsActive(true);
		deviceSpecifications.add(deviceSpecification1);
		deviceSpecificationListWithDeviceTypeCode = new ArrayList<DeviceSpecification>();
		deviceSpecificationListWithDeviceTypeCode.add(deviceSpecification);

	}

	@Test
	public void findDeviceSpecificationByLangugeCodeTest() {
		String languageCode = "ENG";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenReturn(deviceSpecifications);

		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCode(languageCode);
		Assert.assertEquals(deviceSpecificationDtos.get(0).getId(), deviceSpecifications.get(0).getId());
		Assert.assertEquals(deviceSpecificationDtos.get(0).getName(), deviceSpecifications.get(0).getName());

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionTest() {
		List<DeviceSpecification> empityList = new ArrayList<DeviceSpecification>();
		String languageCode = "FRN";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionForNullTest() {
		String languageCode = "FRN";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionInGetAllTest() {
		String languageCode = "eng";
		Mockito.when(deviceSpecificationRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCode(languageCode);

	}

	@Test
	public void findDeviceSpecificationByLangugeCodeAndDeviceTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(languageCode, deviceTypeCode))
				.thenReturn(deviceSpecificationListWithDeviceTypeCode);

		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);
		Assert.assertEquals(deviceSpecificationDtos.get(0).getId(),
				deviceSpecificationListWithDeviceTypeCode.get(0).getId());
		Assert.assertEquals(deviceSpecificationDtos.get(0).getName(),
				deviceSpecificationListWithDeviceTypeCode.get(0).getName());
		Assert.assertEquals(deviceSpecificationDtos.get(0).getDeviceTypeCode(),
				deviceSpecificationListWithDeviceTypeCode.get(0).getDeviceTypeCode());

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionInDeviceSpecificationByDevicTypeCodeTest() {
		List<DeviceSpecification> empityList = new ArrayList<DeviceSpecification>();
		String languageCode = "FRN";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(deviceTypeCode, deviceTypeCode))
				.thenReturn(empityList);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);
	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionnDeviceSpecificationByDevicTypeCodeForNullTest() {
		String languageCode = "FRN";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(deviceTypeCode, deviceTypeCode))
				.thenReturn(null);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionnDeviceSpecificationByDevicTypeCodeTest() {
		String languageCode = "ENG";
		String deviceTypeCode = "operating_sys";
		Mockito.when(deviceSpecificationRepository
				.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(languageCode, deviceTypeCode))
				.thenThrow(DataAccessResourceFailureException.class);
		deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);

	}

}
