
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

import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineResponseDto;

import io.mosip.kernel.masterdata.dto.MachineResponseIdDto;

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
		MachineResponseIdDto machineDetailResponseIdDto =  new MachineResponseIdDto();
		MachineDto machineDetailDto = new MachineDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);

		machineDetailResponseIdDto.setMachineDto(machineDetailDto);
		Mockito.when(macService.getMachineIdLangcode(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(machineDetailResponseIdDto);
		MachineResponseIdDto actual = machineDetailController.getMachineIdLangcode(Mockito.anyString(),
				Mockito.anyString());

		Assert.assertNotNull(actual);
		Assert.assertEquals(machineDetailDto.getId(), actual.getMachineDto().getId());

	}

	@Test
	public void getMachineDetailAllTest() {
		List<MachineDto> machineDetailDtoList = new ArrayList<>();
		MachineDto machineDetailDto = new MachineDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);
		machineDetailDtoList.add(machineDetailDto);
		MachineResponseDto machineDetailResponseDto = new MachineResponseDto();
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		Mockito.when(macService.getMachineAll()).thenReturn(machineDetailResponseDto);
		MachineResponseDto actual = machineDetailController.getMachineAll();

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineDetails().size() > 0);
	}
	
	@Test
	public void getMachineDetailLangTest() {
		MachineResponseDto machineDetailResponseDto =  new MachineResponseDto();

		List<MachineDto> machineDetailDtoList = new ArrayList<>();
		MachineDto machineDetailDto = new MachineDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);
		machineDetailDtoList.add(machineDetailDto);
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		Mockito.when(macService.getMachineLangcode(Mockito.anyString()))
				.thenReturn(machineDetailResponseDto);
		MachineResponseDto actual = machineDetailController.getMachineLangcode(Mockito.anyString());

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineDetails().size() > 0);

	}
}
