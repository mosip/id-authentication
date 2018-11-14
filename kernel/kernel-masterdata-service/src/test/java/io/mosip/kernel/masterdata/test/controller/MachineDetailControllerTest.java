
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

import io.mosip.kernel.masterdata.controller.MachineDetailController;
import io.mosip.kernel.masterdata.dto.MachineDetailDto;
import io.mosip.kernel.masterdata.service.MachineDetailService;

@RunWith(MockitoJUnitRunner.class)
public class MachineDetailControllerTest {

	@InjectMocks
	private MachineDetailController machineDetailController;

	@Mock
	private MachineDetailService macService;

	@Before
	public void setUp() {
		machineDetailController = new MachineDetailController();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetMachineDetailIdLang() {
		MachineDetailDto machineDetailDto = new MachineDetailDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setActive(true);

		Mockito.when(macService.getMachineDetailIdLang(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(machineDetailDto);
		MachineDetailDto actual = machineDetailController.getMachineDetailIdLang(Mockito.anyString(),
				Mockito.anyString());

		Assert.assertNotNull(actual);
		Assert.assertEquals(machineDetailDto.getId(), actual.getId());

	}

	@Test
	public void testGetMachineDetailAll() {
		List<MachineDetailDto> machineDetailDtoList = new ArrayList<>();
		MachineDetailDto machineDetailDto = new MachineDetailDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setActive(true);
		machineDetailDtoList.add(machineDetailDto);
		Mockito.when(macService.getMachineDetailAll()).thenReturn(machineDetailDtoList);
		List<MachineDetailDto> actual = machineDetailController.getMachineDetailAll();

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.size() > 0);
	}
}
