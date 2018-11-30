
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

import io.mosip.kernel.masterdata.controller.MachineController;
import io.mosip.kernel.masterdata.dto.MachineDetailDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseIdDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.service.MachineService;

@RunWith(MockitoJUnitRunner.class)
public class MachineDetailControllerTest {

	@InjectMocks
	private MachineController machineDetailController;

	@Mock
	private MachineService macService;

	@Before
	public void setUp() {
		machineDetailController = new MachineController();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getMachineDetailIdLangTest() {
		MachineDetailResponseIdDto machineDetailResponseIdDto =  new MachineDetailResponseIdDto();
		MachineDetailDto machineDetailDto = new MachineDetailDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);

		machineDetailResponseIdDto.setMachineDetail(machineDetailDto);
		Mockito.when(macService.getMachineDetailIdLang(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(machineDetailResponseIdDto);
		MachineDetailResponseIdDto actual = machineDetailController.getMachineDetailIdLang(Mockito.anyString(),
				Mockito.anyString());

		Assert.assertNotNull(actual);
		Assert.assertEquals(machineDetailDto.getId(), actual.getMachineDetail().getId());

	}

	@Test
	public void getMachineDetailAllTest() {
		List<MachineDetailDto> machineDetailDtoList = new ArrayList<>();
		MachineDetailDto machineDetailDto = new MachineDetailDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);
		machineDetailDtoList.add(machineDetailDto);
		MachineResponseDto machineDetailResponseDto = new MachineResponseDto();
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		Mockito.when(macService.getMachineDetailAll()).thenReturn(machineDetailResponseDto);
		MachineResponseDto actual = machineDetailController.getMachineDetailAll();

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineDetails().size() > 0);
	}
	
	@Test
	public void getMachineDetailLangTest() {
		MachineResponseDto machineDetailResponseDto =  new MachineResponseDto();
		List<MachineDetailDto> machineDetailDtoList = new ArrayList<>();
		MachineDetailDto machineDetailDto = new MachineDetailDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);
		machineDetailDtoList.add(machineDetailDto);
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		Mockito.when(macService.getMachineDetailLang(Mockito.anyString()))
				.thenReturn(machineDetailResponseDto);
		MachineResponseDto actual = machineDetailController.getMachineDetailLang(Mockito.anyString());

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineDetails().size() > 0);

	}
}
