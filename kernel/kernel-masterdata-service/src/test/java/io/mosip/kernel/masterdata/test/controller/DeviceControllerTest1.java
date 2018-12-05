
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

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
/*import io.mosip.kernel.masterdata.dto.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceResponseDto;*/
import io.mosip.kernel.masterdata.service.DeviceService;

/*@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = DeviceController.class, secure = false)*/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeviceControllerTest1 {

                
                @Autowired
                public MockMvc mockMvc;
                
                @MockBean
                private DeviceService deviceService;
                
                DeviceResponseDto deviceResponseDto;
                DeviceLangCodeResponseDto deviceLangCodeResponseDto;
                
                @Before
                public void setUp() {
                                deviceResponseDto = new DeviceResponseDto();
                                List<DeviceDto> deviceDtos = new ArrayList<>();
                                DeviceDto  deviceDto = new DeviceDto();
                                deviceDto.setCode("1000");
                                deviceDto.setName("HP");
                                deviceDto.setLangCode("ENG");
                                deviceDto.setDeviceSpecId("laptopId");
                                deviceDtos.add(deviceDto);
                                deviceResponseDto.setDevices(deviceDtos);       
                                
                                deviceLangCodeResponseDto = new DeviceLangCodeResponseDto();
                                List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = new ArrayList<>();
                                DeviceLangCodeDtypeDto deviceLangCodeDtypeDto = new DeviceLangCodeDtypeDto();
                                deviceLangCodeDtypeDto.setId("1000");
                                deviceLangCodeDtypeDto.setName("HP");
                                deviceLangCodeDtypeDto.setName("ENG");
                                deviceLangCodeDtypeDto.setDeviceTypeCode("LaptopCode");
                                deviceLangCodeDtypeDtoList.add(deviceLangCodeDtypeDto);
                                deviceLangCodeResponseDto.setDevices(deviceLangCodeDtypeDtoList);
                                
                }
                
                
                @Test
                public void getDeviceLangTest() throws Exception {
                                Mockito.when(deviceService.getDeviceLangCode(Mockito.anyString())).thenReturn(deviceResponseDto);
                                mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/devices/ENG")).andExpect(status().isOk());
                }
                
                @Test
                public void getDeviceLangCodeAndDeviceTypeTest() throws Exception  {
                                Mockito.when(deviceService.getDeviceLangCodeAndDeviceType(Mockito.anyString(), Mockito.anyString())).thenReturn(deviceLangCodeResponseDto);
                                mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/devices/ENG/LaptopCode")).andExpect(status().isOk());
                }
                
                
}

