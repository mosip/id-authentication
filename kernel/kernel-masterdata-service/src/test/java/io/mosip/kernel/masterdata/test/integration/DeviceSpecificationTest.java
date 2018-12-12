package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.http.MediaType;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;



@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceSpecificationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;
	
	private ObjectMapper mapper;
	private DeviceSpecification deviceSpecification;
	private DeviceSpecificationDto deviceSpecificationDto;
	private RequestDto<DeviceSpecificationDto> requestDto;
	
	@Before
	public void DeviceSpecificationRepositorySetUp() {
		mapper = new ObjectMapper();

		
		deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("1000");
		deviceSpecification.setLangCode("ENG");
		deviceSpecification.setName("laptop");
		deviceSpecification.setIsActive(true);
		deviceSpecification.setDescription("HP Description" );
		deviceSpecification.setBrand("HP");
		deviceSpecification.setDeviceTypeCode("1231");
		deviceSpecification.setLangCode("ENG");
		deviceSpecification.setMinDriverversion("version 0.1");
		deviceSpecification.setModel("3168ngw");
	
		
		deviceSpecificationDto = new DeviceSpecificationDto();
		MapperUtils.map(deviceSpecification, deviceSpecificationDto);

		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.DeviceSpecificationcode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceSpecificationDto);

	}
	
	@Test
	public void createDeviceSpecificationTest() throws Exception {

		String deviceSpecificationJson = mapper.writeValueAsString(requestDto);

		when(deviceSpecificationRepository.create(Mockito.any())).thenReturn(deviceSpecification);
		mockMvc.perform(post("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON).content(deviceSpecificationJson))
		.andExpect(status().isCreated());
		
	}
	
	@Test
	public void createDeviceSpecificationExceptionTest() throws Exception {
		
		String DeviceSpecificationJson = mapper.writeValueAsString(requestDto);

		Mockito.when(deviceSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/devicespecifications").contentType(MediaType.APPLICATION_JSON).content(DeviceSpecificationJson))
				.andExpect(status().isInternalServerError());
		
	}

}

