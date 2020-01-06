package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.masterdata.dto.DeRegisterDeviceRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceDataDto;
import io.mosip.kernel.masterdata.dto.DeviceDeRegDto;
import io.mosip.kernel.masterdata.dto.DeviceInfoDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.entity.DeviceRegister;
import io.mosip.kernel.masterdata.repository.DeviceRegisterHistoryRepository;
import io.mosip.kernel.masterdata.repository.DeviceRegisterRepository;
import io.mosip.kernel.masterdata.test.TestBootApplication;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG, printOnlyOnFailure = false)
public class DeviceRegisterIntergrationTest {

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Reference to {@link DeviceRegisterRepository}.
	 */
	@MockBean
	private DeviceRegisterRepository deviceRegisterRepository;
	/**
	 * Reference to {@link DeviceRegisterHistoryRepository}.
	 */
	@MockBean
	private DeviceRegisterHistoryRepository deviceRegisterHistoryRepository;

	@Autowired
	private ObjectMapper objectMapper;
	private DeviceRegisterDto deviceRegisterDto;
	private DeviceDataDto deviceDataDto;
	private DeviceInfoDto infoDto;
	private DeviceRegister deviceRegister;
	private DeRegisterDeviceRequestDto deRegisterDeviceRequestDto;
	private DeviceDeRegDto deviceDeRegDto;

	@Before
	public void setUp() {
		deviceRegisterDto = new DeviceRegisterDto();
		deviceDataDto = new DeviceDataDto();
		deviceRegister = new DeviceRegister();
		deviceDeRegDto = new DeviceDeRegDto();
		deRegisterDeviceRequestDto = new DeRegisterDeviceRequestDto();
		deviceDeRegDto.setDeviceCode("123456");
		deviceDeRegDto.setTimestamp(LocalDateTime.now());
		deviceRegister.setUpdatedDateTime(LocalDateTime.now());
		deRegisterDeviceRequestDto.setDevice(deviceDeRegDto);
		deRegisterDeviceRequestDto.setSignature("13456");
		infoDto = new DeviceInfoDto();
		infoDto.setTimestamp(LocalDateTime.now());
		deviceDataDto.setDeviceCode("1232344");
		deviceDataDto.setDeviceId("123");
		deviceDataDto.setDeviceInfo(infoDto);
		deviceRegisterDto.setDeviceData(deviceDataDto);
		deviceRegisterDto.setDpSignature("1234567");

		
	}
	
	private void successTest() {
		when(deviceRegisterRepository.findById(Mockito.any(), Mockito.anyString())).thenReturn(deviceRegister);
		when(deviceRegisterRepository.update(Mockito.any())).thenReturn(deviceRegister);
		when(deviceRegisterHistoryRepository.create(Mockito.any())).thenReturn(null);
	}

	@Test
	@WithUserDetails("device-provider")
	public void registerDeviceTest() throws Exception {
		mockMvc.perform(post("/device/register").content(objectMapper.writeValueAsString(deviceRegisterDto))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

//	@Test
//	@WithUserDetails("device-provider")
//	public void deRegisterDeviceTest() throws JsonProcessingException, Exception {
//		mockMvc.perform(
//				delete("/device/deregister").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
//						.contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void updateDevicesTest() throws JsonProcessingException, Exception {
		successTest();
		mockMvc.perform(
				put("/device/update/status").param("devicecode", "10001").param("statuscode", "Registered").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void updateDevicesInvalidStatusTest() throws JsonProcessingException, Exception {
		successTest();
		mockMvc.perform(
				put("/device/update/status").param("devicecode", "10001").param("statuscode", "Registereds").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void updateDevicesInvalidStatusDbExceptionTest() throws JsonProcessingException, Exception {
		successTest();
		when(deviceRegisterRepository.findById(Mockito.any(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(
				put("/device/update/status").param("devicecode", "10001").param("statuscode", "Registered").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void updateDevicesInvalidStatusDataNotFoundTest() throws JsonProcessingException, Exception {
		successTest();
		when(deviceRegisterRepository.findById(Mockito.any(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(
				put("/device/update/status").param("devicecode", "10001").param("statuscode", "Registered").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void updateDevicesInvalidStatusDbUpdateExceptionTest() throws JsonProcessingException, Exception {
		successTest();
		when(deviceRegisterRepository.update(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(
				put("/device/update/status").param("devicecode", "10001").param("statuscode", "Registered").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void updateDevicesInvalidStatusDbCreateExceptionTest() throws JsonProcessingException, Exception {
		successTest();
		when(deviceRegisterHistoryRepository.create(Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(
				put("/device/update/status").param("devicecode", "10001").param("statuscode", "Registered").content(objectMapper.writeValueAsString(deRegisterDeviceRequestDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}
	
	
}
