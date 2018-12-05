package io.mosip.kernel.masterdata.test.controller;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;

/*@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = DeviceSpecificationController.class, secure = false)*/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeviceSpecificationControllerTest1 {
                
                @Autowired
                public MockMvc mockMvc;
                
                @MockBean
                private DeviceSpecificationService deviceSpecificationService;
                
                List<DeviceSpecificationDto> deviceSpecificationDtos;
                
                @Before
                public void setUp() {
                                
                                deviceSpecificationDtos = new ArrayList<>();
                                DeviceSpecificationDto deviceSpecificationDto = new DeviceSpecificationDto();
                                deviceSpecificationDto.setId("1000");
                                deviceSpecificationDto.setName("Laptop");
                                deviceSpecificationDto.setDeviceTypeCode("LaptopCode");
                                deviceSpecificationDto.setLangCode("ENG");
                                deviceSpecificationDto.setModel("HP");
                                deviceSpecificationDtos.add(deviceSpecificationDto);
                                
                }
                
                
                @Test
                public void getDeviceSpecificationByLanguageCodeTest() throws Exception {
                Mockito.when(deviceSpecificationService.findDeviceSpecificationByLangugeCode(Mockito.anyString())).thenReturn(deviceSpecificationDtos);
                                mockMvc.perform(MockMvcRequestBuilders.get("/devicespecifications/ENG")).andExpect(status().isOk());
                }
                
                @Test
                public void getDeviceSpecificationByLanguageCodeAndDeviceTypeCodeTest() throws Exception {
                Mockito.when(deviceSpecificationService.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(Mockito.anyString(), Mockito.anyString())).thenReturn(deviceSpecificationDtos);
                mockMvc.perform(MockMvcRequestBuilders.get("/devicespecifications/ENG/LaptopCode")).andExpect(status().isOk());
                }

}

