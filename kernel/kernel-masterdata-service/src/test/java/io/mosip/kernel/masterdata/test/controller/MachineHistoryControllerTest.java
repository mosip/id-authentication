
package io.mosip.kernel.masterdata.test.controller;



import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.controller.MachineHistoryController;
import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import io.mosip.kernel.masterdata.dto.MachineHistoryResponseDto;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.service.MachineHistoryService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;
import io.mosip.kernel.masterdata.utils.StringToLocalDateTimeConverter;

@RunWith(MockitoJUnitRunner.class)
public class MachineHistoryControllerTest {

	@InjectMocks
	private MachineHistoryController machineHistoryController;

	@Mock
	private MachineHistoryService macService;
	
	@Autowired
	MockMvc mockMvc;
	
	@Mock
	private MachineHistoryRepository machineHistoryRepository;
	
	@Mock
	private StringToLocalDateTimeConverter stringToLocalDateTimeConverter;
	
	@Mock
	private ObjectMapperUtil objMapper;
	
	@Before
	public void setUp() {
		machineHistoryController = new MachineHistoryController();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetMachineHistoryIdLangEffDTim() {
		List<MachineHistoryDto> machineHistoryDtoList = new ArrayList<>();
		MachineHistoryDto machineHistoryDto = new MachineHistoryDto();
		machineHistoryDto.setId("1000");
		machineHistoryDto.setName("HP");
		machineHistoryDto.setSerialNum("1234567890");
		machineHistoryDto.setMacAddress("100.100.100.80");
		machineHistoryDto.setLangCode("ENG");
		machineHistoryDto.setIsActive(true);
		machineHistoryDtoList.add(machineHistoryDto);

		MachineHistoryResponseDto machineHistoryResponseDto = new MachineHistoryResponseDto();
		machineHistoryResponseDto.setMachineHistoryDetails(machineHistoryDtoList);
		Mockito.when(
				macService.getMachineHistroyIdLangEffDTime(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenReturn(machineHistoryResponseDto);
		MachineHistoryResponseDto actual = machineHistoryController.getMachineHistoryIdLangEff(Mockito.anyString(),
				Mockito.anyString(), Mockito.any());

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineHistoryDetails().size() > 0);

	}
	
}
