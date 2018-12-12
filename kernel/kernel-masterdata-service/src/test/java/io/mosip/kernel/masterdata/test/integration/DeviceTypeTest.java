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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceTypeTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private DeviceTypeRepository deviceTypeRepository;
	
	private ObjectMapper mapper;
	private DeviceType deviceType;
	private DeviceTypeDto deviceTypeDto;
	private RequestDto<DeviceTypeDto> requestDto;
	
	@Before
	public void DevicetypeSetUp() {
		mapper = new ObjectMapper();

		
		deviceType = new DeviceType();
		deviceType.setCode("1000");
		deviceType.setLangCode("ENG");
		deviceType.setName("Laptop");
		deviceType.setIsActive(true);
		deviceType.setDescription("Laptop Description" );


		
		deviceTypeDto = new DeviceTypeDto();
		MapperUtils.map(deviceType, deviceTypeDto);

		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.Devicetypecode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceTypeDto);

	}
	
	@Test
	public void createDeviceTypeTest() throws Exception {

		String DeviceTypeJson = mapper.writeValueAsString(requestDto);

		when(deviceTypeRepository.create(Mockito.any())).thenReturn(deviceType);
		mockMvc.perform(post("/v1.0/devicetypes").contentType(MediaType.APPLICATION_JSON).content(DeviceTypeJson))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void createDeviceTypeExceptionTest() throws Exception {
		
		String DeviceTypeJson = mapper.writeValueAsString(requestDto);

		Mockito.when(deviceTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/devicetypes").contentType(MediaType.APPLICATION_JSON).content(DeviceTypeJson))
				.andExpect(status().isInternalServerError());
	}

}

