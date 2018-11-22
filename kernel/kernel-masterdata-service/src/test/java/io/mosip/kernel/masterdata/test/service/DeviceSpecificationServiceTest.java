package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.DeviceSpecPostResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationListDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceSpecificationServiceTest {

	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;

	@MockBean
	private MetaDataUtils metaUtils;

	@Autowired
	private DeviceSpecificationService deviceSpecificationService;
	
	List<DeviceSpecification> deviceSpecificationList ;
	DeviceSpecification deviceSpecification;
	
	DeviceSpecificationRequestDto deviceSpecificationRequestDto ;
	DeviceSpecificationListDto deviceSpecificationListDto;
	List<DeviceSpecificationDto> deviceSpecificationDtos ;
	DeviceSpecificationDto deviceSpecificationDto ;
	
	@Before
	public void setUp() {
		

		deviceSpecificationList = new ArrayList<>();
		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("100");
		deviceSpecification.setDeviceTypeCode("Laptop");
		deviceSpecification.setLangCode("ENG");
		deviceSpecificationList.add(deviceSpecification);
		
		deviceSpecificationRequestDto = new DeviceSpecificationRequestDto();
		deviceSpecificationListDto = new DeviceSpecificationListDto();
		deviceSpecificationDtos = new ArrayList<>();
		deviceSpecificationDto = new DeviceSpecificationDto();
		deviceSpecificationDto.setId("100");
		deviceSpecificationDto.setDeviceTypeCode("Laptop");
		deviceSpecificationDto.setLangCode("ENG");
		deviceSpecificationDtos.add(deviceSpecificationDto);
		deviceSpecificationListDto.setDeviceSpecificationDtos(deviceSpecificationDtos);
		deviceSpecificationRequestDto.setRequest(deviceSpecificationListDto);
	}
	
	@Test
	public void addDeviceSpecificationsTest() {
		Mockito.when(deviceSpecificationRepository.saveAll(Mockito.any())).thenReturn(deviceSpecificationList);
		DeviceSpecPostResponseDto deviceSpecPostResponseDto = deviceSpecificationService.addDeviceSpecifications(deviceSpecificationRequestDto);
		assertEquals(deviceSpecificationListDto.getDeviceSpecificationDtos().get(0).getId(), deviceSpecPostResponseDto.getResults().get(0).getId());
	}
	
	
	@Test(expected = MasterDataServiceException.class)
	public void testaddDeviceSpecificationsThrowsDataAccessException() {
		Mockito.when(deviceSpecificationRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		deviceSpecificationService.addDeviceSpecifications(deviceSpecificationRequestDto);
	}
}




