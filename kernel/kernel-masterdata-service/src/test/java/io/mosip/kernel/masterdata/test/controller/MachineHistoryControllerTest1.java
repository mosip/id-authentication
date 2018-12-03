
package io.mosip.kernel.masterdata.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.Month;
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

import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import io.mosip.kernel.masterdata.service.MachineHistoryService;

import io.mosip.kernel.masterdata.dto.getresponse.MachineHistoryResponseDto;

/*@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value=MachineHistoryController.class, secure=false)*/
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MachineHistoryControllerTest1 {
                
                @Autowired
                public MockMvc mockMvc;
                
                @MockBean
                private MachineHistoryService machineHistoryService;
                
                MachineHistoryResponseDto machineHistoryResponseDto;
                
                @Before
                public void setUP() {
                                machineHistoryResponseDto = new MachineHistoryResponseDto();
                                List<MachineHistoryDto> machineHistoryDetails = new ArrayList<>();
                                MachineHistoryDto machineHistoryDto = new MachineHistoryDto();
                                machineHistoryDto.setId("1000");
                                machineHistoryDto.setLangCode("ENG");
                                LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
                                machineHistoryDto.setEffectDtimes(specificDate);
                                machineHistoryDetails.add(machineHistoryDto);
                                machineHistoryResponseDto.setMachineHistoryDetails(machineHistoryDetails);
                }
                
                @Test
                public void getMachineHistroyIdLangEffDTimeTest() throws Exception {
                                Mockito.when(machineHistoryService.getMachineHistroyIdLangEffDTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(machineHistoryResponseDto);
                                mockMvc.perform(MockMvcRequestBuilders.get("/machineshistories/1000/ENG/2018-01-01T10:10:30.222Z")).andExpect(status().isOk());
                }
                
}