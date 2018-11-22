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

import io.mosip.kernel.masterdata.controller.DeviceTypeController;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeListDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.service.DeviceTypeService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value=DeviceTypeController.class, secure=false)
public class DeviceTypeControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	DeviceTypeService deviceTypeService;
	
	DeviceTypeRequestDto reqTypeDto;
	DeviceTypeListDto devTypeListDto;
	List<DeviceTypeDto> devTypeList;
	DeviceTypeDto devTypeDto;
	
	PostResponseDto resDto;
	List<CodeAndLanguageCodeId> codeLangCodeList ;
	CodeAndLanguageCodeId codeLangCode ;
	
	
	@Before 
	public void setUP() {
		reqTypeDto = new DeviceTypeRequestDto();
		devTypeListDto = new DeviceTypeListDto();
		devTypeList = new ArrayList<>();
		devTypeDto =new DeviceTypeDto();
		devTypeDto.setCode("laptop");
		devTypeDto.setLangCode("ENG");
		devTypeDto.setName("HP");
		devTypeDto.setDescription("Laptop disc");
		devTypeList.add(devTypeDto);
		devTypeListDto.setDeviceTypeDtos(devTypeList);
		reqTypeDto.setRequest(devTypeListDto);
		
		resDto = new PostResponseDto();
		codeLangCodeList = new ArrayList<>();
		codeLangCode = new CodeAndLanguageCodeId();
		codeLangCode.setCode("laptop");
		codeLangCode.setLangCode("ENG");
		codeLangCodeList.add(codeLangCode);
		resDto.setResults(codeLangCodeList);
		
	}
	@Test
	public void addDeviceTypes() throws Exception {
	
		String inputJson = this.maptoJson(reqTypeDto);
		Mockito.when(deviceTypeService.addDeviceTypes(reqTypeDto)).thenReturn(resDto);
		
		MvcResult result =mockMvc.perform(MockMvcRequestBuilders.post("/devicetypes/")
				.accept(MediaType.APPLICATION_JSON).content(inputJson)
				.contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		MockHttpServletResponse response =result.getResponse();
		String outputJson = response.getContentAsString();
		assertNotNull(outputJson);	
	}
	private String maptoJson(Object object)throws  JsonProcessingException{
		ObjectMapper objectMap = new ObjectMapper();
		return objectMap.writeValueAsString(object);
	}
	

}
