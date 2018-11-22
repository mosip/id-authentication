package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;

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
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeListDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceTypeServiceImplTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private MetaDataUtils metaUtils;

	@MockBean
	private DeviceTypeRepository deviceTypeRepository;

	

	@Autowired
	private DeviceTypeService deviceTypeService;

	DeviceTypeRequestDto reqTypeDto;
	DeviceTypeListDto request;
	List<DeviceTypeDto> deviceTypeDtoList;
	DeviceTypeDto deviceTypeDto;

	List<DeviceType> deviceTypeList;
	DeviceType deviceType;
	List<CodeAndLanguageCodeId> codeLangCodeIds;
	CodeAndLanguageCodeId codeAndLanguageCodeId;

	@Before
	public void setUp() {

		reqTypeDto = new DeviceTypeRequestDto();
		request = new DeviceTypeListDto();
		deviceTypeDtoList = new ArrayList<>();
		deviceTypeDto = new DeviceTypeDto();

		deviceTypeDto.setCode("Laptop");
		deviceTypeDto.setCode("Laptop");
		deviceTypeDto.setLangCode("ENG");
		deviceTypeDto.setName("HP");
		deviceTypeDto.setDescription("Laptop Desc");
		deviceTypeDtoList.add(deviceTypeDto);
		request.setDeviceTypeDtos(deviceTypeDtoList);
		reqTypeDto.setRequest(request);

		deviceTypeList = new ArrayList<>();
		deviceType = new DeviceType();
		deviceType.setCode("Laptop");
		deviceType.setLangCode("ENG");
		deviceType.setName("HP");
		deviceType.setDescription("Laptop Desc");
		deviceTypeList.add(deviceType);

		codeLangCodeIds = new ArrayList<>();
		codeAndLanguageCodeId = new CodeAndLanguageCodeId();
		codeAndLanguageCodeId.setCode("Laptop");
		codeAndLanguageCodeId.setLangCode("ENG");
		codeLangCodeIds.add(codeAndLanguageCodeId);

	}

	@Test
	public void addDeviceTypesTest() {
		Mockito.when(deviceTypeRepository.saveAll(Mockito.any())).thenReturn(deviceTypeList);
		PostResponseDto postResponseDto = deviceTypeService.addDeviceTypes(reqTypeDto);
		assertEquals(request.getDeviceTypeDtos().get(0).getCode(), postResponseDto.getResults().get(0).getCode());
	}

	
	
	@Test(expected = MasterDataServiceException.class)
	public void testaddDeviceTypesThrowsDataAccessException() {
		Mockito.when(deviceTypeRepository.saveAll(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		deviceTypeService.addDeviceTypes(reqTypeDto);
	}
}