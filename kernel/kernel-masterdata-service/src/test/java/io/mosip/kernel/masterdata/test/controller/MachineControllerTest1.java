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

import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.MachineResponseIdDto;
import io.mosip.kernel.masterdata.service.MachineService;



/*@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = MachineDetailController.class, secure = false)*/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MachineControllerTest1 {
                
                
                @Autowired
                public MockMvc mockMvc;
                
                @MockBean
                private MachineService machineService;
                
                
                MachineResponseIdDto machineResponseIdDto;
                MachineResponseDto machineResponseDto;
                
                @Before
                public void setUp() {
                                machineResponseIdDto =  new MachineResponseIdDto();
                                machineResponseDto = new MachineResponseDto();
                                List<MachineDto> machineDtoList = new ArrayList<>();
                                MachineDto machineDto = new MachineDto();
                                machineDto.setId("1000");
                                machineDto.setName("HP");
                                machineDto.setSerialNum("1234567890");
                                machineDto.setMacAddress("100.100.100.80");
                                machineDto.setLangCode("ENG");
                                machineDto.setIsActive(true);
                                machineDtoList.add(machineDto);
                                machineResponseIdDto.setMachine(machineDto);
                                machineResponseDto.setMachines(machineDtoList);
                }
                
                @Test
                public void getMachineIdLangcodeTest() throws Exception {
                                Mockito.when(machineService.getMachineIdLangcode(Mockito.anyString(), Mockito.anyString())).thenReturn(machineResponseIdDto);
                                mockMvc.perform(MockMvcRequestBuilders.get("/machines/1000/ENG")).andExpect(status().isOk());
                }
                
                
                @Test
                public void getMachineLangcodeTest() throws Exception {
                Mockito.when(machineService.getMachineLangcode(Mockito.anyString())).thenReturn(machineResponseDto);
                                mockMvc.perform(MockMvcRequestBuilders.get("/machines/ENG")).andExpect(status().isOk());
                }
                
                
                @Test
                public void getMachineAllTest() throws Exception {
                                Mockito.when(machineService.getMachineAll()).thenReturn(machineResponseDto);
                                mockMvc.perform(MockMvcRequestBuilders.get("/machines")).andExpect(status().isOk());
                }
                
                

}
