package io.mosip.kernel.masterdata.test.controller;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.masterdata.controller.DeviceSpecificationController;
import io.mosip.kernel.masterdata.dto.DeviceSpecPostResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationListDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeCodeAndLanguageCode;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = DeviceSpecificationController.class, secure = false)
public class DeviceSpecificationControllerCreateTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	DeviceSpecificationService deviceSpecificationService;

	DeviceSpecificationRequestDto requestDto;
	DeviceSpecificationListDto devSepcList;
	List<DeviceSpecificationDto> devDecDtoList;
	DeviceSpecificationDto devSpecDto;
	DeviceSpecPostResponseDto responseDto;
	List<DeviceTypeCodeAndLanguageCode> devTypes;
	DeviceTypeCodeAndLanguageCode devType;

	@Before
	public void setUp() {
		requestDto = new DeviceSpecificationRequestDto();
		devSepcList = new DeviceSpecificationListDto();
		devDecDtoList = new ArrayList<>();
		devSpecDto = new DeviceSpecificationDto();
		devSpecDto.setId("100");
		devSpecDto.setName("HP");
		devSpecDto.setLangCode("ENG");
		devSpecDto.setDeviceTypeCode("laptop");
		devDecDtoList.add(devSpecDto);
		devSepcList.setDeviceSpecificationDtos(devDecDtoList);
		requestDto.setRequest(devSepcList);

		responseDto = new DeviceSpecPostResponseDto();
		devTypes = new ArrayList<>();
		devType = new DeviceTypeCodeAndLanguageCode();
		devType.setId("100");
		devType.setLangCode("ENG");
		devType.setDeviceTypeCode("laptop");
		devTypes.add(devType);
		responseDto.setResults(devTypes);

	}

	@Test
	public void testAddDeviceSpecifications() throws Exception {

		String inputJson = this.maptoJson(requestDto);
		Mockito.when(
				deviceSpecificationService.addDeviceSpecifications(Mockito.any(DeviceSpecificationRequestDto.class)))
				.thenReturn(responseDto);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/devicespecifications/")
				.accept(MediaType.APPLICATION_JSON).content(inputJson).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		MockHttpServletResponse response = result.getResponse();
		String outputJson = response.getContentAsString();
		assertNotNull(outputJson);

	}

	private String maptoJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMap = new ObjectMapper();
		return objectMap.writeValueAsString(object);
	}
}
