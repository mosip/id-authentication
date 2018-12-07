package io.mosip.kernel.masterdata.test.service;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.repository.MachineRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MachineTest {
	
	 @Autowired
     public MockMvc mockMvc;
	 
	@MockBean
	private MachineRepository machineRepository;
	
	public List<Machine> machineList;
	
	@Before
	public void setUp() {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		machineList = new ArrayList<>();
		Machine machine = new Machine();
		machine.setId("1000");
        machine.setName("HP");
        machine.setSerialNum("1234567890");
        machine.setMacAddress("100.100.100.80");
        machine.setLangCode("ENG");
        machine.setIsActive(true);
        machine.setValidityDateTime(specificDate);
        machineList.add(machine);
	}
	
	@Test
	public void getMachineAllSuccessTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(machineList);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isOk());
	}

}
