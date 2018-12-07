package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceSpecificationTest {
	
	@Autowired
    public MockMvc mockMvc;
	 
	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;
	
	List<DeviceSpecification> deviceSpecList;
	
	@Before
	public void setUp() {
		
		deviceSpecList = new ArrayList<>();
		DeviceSpecification deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("1000");
		deviceSpecification.setName("Laptop");
		deviceSpecification.setBrand("HP");
		deviceSpecification.setModel("G-Series");
		deviceSpecification.setMinDriverversion("version 7");
		deviceSpecification.setDescription("HP Laptop");
		deviceSpecification.setIsActive(true);
		deviceSpecList.add(deviceSpecification);
	}
	
	
	@Test
	public void findDeviceSpecLangcodeSuccessTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString())).thenReturn(deviceSpecList);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}","ENG")).andExpect(status().isOk());
	}
	
	@Test
	public void findDeviceSpecLangcodeNullResponseTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG")).andExpect(status().isNotFound());
	}
	
	@Test
	public void  findDeviceSpecLangcodeFetchExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}
	

	@Test
	public void findDeviceSpecByLangCodeAndDevTypeCodeSuccessTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString())).thenReturn(deviceSpecList);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}","ENG", "laptop")).andExpect(status().isOk());
	}
	
	@Test
	public void findDeviceSpecByLangCodeAndDevTypeCodeNullResponseTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}", "ENG","laptop")).andExpect(status().isNotFound());
	}
	
	@Test
	public void  findDeviceSpecByLangCodeAndDevTypeCodeFetchExceptionTest() throws Exception {
		when(deviceSpecificationRepository.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/devicespecifications/{langcode}/{devicetypecode}", "ENG","laptop")).andExpect(status().isInternalServerError());
	}
	
	

}
