
package io.mosip.kernel.masterdata.test.service;

import static org.mockito.Mockito.doReturn;

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
import org.springframework.dao.DataRetrievalFailureException;


import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineResponseDto;


import io.mosip.kernel.masterdata.dto.MachineResponseIdDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.service.impl.MachineServiceImpl;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@RunWith(MockitoJUnitRunner.class)
public class MachineDetailServiceImplTest {

	@InjectMocks
	private MachineServiceImpl machineDetailServiceImpl;

	@Mock
	private MachineRepository machineDetailsRepository;

	@Mock
	private MapperUtils objectMapperUtil;

	@Before
	public void setUp() {
		machineDetailServiceImpl = new MachineServiceImpl();
		MockitoAnnotations.initMocks(this);
	}

	public Machine machineDetail = new Machine();
	public List<Machine> machineDetailList = new ArrayList<>();

	@Test
	public void getMachineDetailIdLangTest() {
		MachineDto machineDetailDto = new MachineDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);

		Machine machineDetail = new Machine();
		machineDetail.setId("1000");
		machineDetail.setName("HP");
		machineDetail.setSerialNum("1234567890");
		machineDetail.setMacAddress("100.100.100.80");
		machineDetail.setLangCode("ENG");
		machineDetail.setIsActive(true);
		Mockito.when(machineDetailsRepository
				.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(machineDetail);
		Mockito.when(objectMapperUtil.map(machineDetail, MachineDto.class)).thenReturn(machineDetailDto);
		MachineResponseIdDto actual = machineDetailServiceImpl.getMachineIdLangcode(Mockito.anyString(),
				Mockito.anyString());
		Assert.assertNotNull(actual);
		Assert.assertEquals(machineDetailDto.getId(), actual.getMachineDto().getId());

	}

	@Test(expected = DataNotFoundException.class)
	public void getMachineDetailIdLangThrowsMachineNotFoundExceptionTest() {
		Mockito.when(machineDetailsRepository
				.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		machineDetailServiceImpl.getMachineIdLangcode("1000", "ENG");

	}

	@Test(expected = MasterDataServiceException.class)
	public void getMachineDetailIdLangThrowsDataAccessExceptionTest() {
		Mockito.when(machineDetailsRepository
				.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		machineDetailServiceImpl.getMachineIdLangcode("1000", "ENG");

	}

	@Test
	public void getMachineDetailAllTest() {
		List<MachineDto> machineDetailDtoList = new ArrayList<MachineDto>();
		MachineDto machineDetailDto = new MachineDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);
		machineDetailDtoList.add(machineDetailDto);

		Machine machineDetail = new Machine();
		machineDetail.setId("1000");
		machineDetail.setName("HP");
		machineDetail.setSerialNum("1234567890");
		machineDetail.setMacAddress("100.100.100.80");
		machineDetail.setLangCode("ENG");
		machineDetail.setIsActive(true);

		List<Machine> machineDetailList = new ArrayList<Machine>();
		machineDetailList.add(machineDetail);
		Mockito.when(machineDetailsRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(machineDetailList);
		Mockito.when(objectMapperUtil.mapAll(machineDetailList, MachineDto.class))
				.thenReturn(machineDetailDtoList);
		MachineResponseDto actual = machineDetailServiceImpl.getMachineAll();

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineDetails().size() > 0);

	}

	@Test(expected = DataNotFoundException.class)
	public void getMachineDetailAllThrowsMachineNotFoundExcetionTest() {
		Mockito.when(machineDetailsRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataNotFoundException.class);
		machineDetailServiceImpl.getMachineAll();

	}

	@Test(expected = MasterDataServiceException.class)
	public void getMachineDetailAllThrowsDataAccessExcetionTest() {
		Mockito.when(machineDetailsRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataRetrievalFailureException.class);
		machineDetailServiceImpl.getMachineAll();

	}

	@Test(expected = DataNotFoundException.class)
	public void getMachineDetailAllThrowsMachineDetailNotFoundExceptionTest() {
		doReturn(null).when(machineDetailsRepository).findAllByIsDeletedFalseOrIsDeletedIsNull();
		machineDetailServiceImpl.getMachineAll();
	}
	
	
	@Test
	public void getMachineDetailLangTest() {
		List<MachineDto> machineDetailDtoList = new ArrayList<MachineDto>();
		MachineDto machineDetailDto = new MachineDto();
		machineDetailDto.setId("1000");
		machineDetailDto.setName("HP");
		machineDetailDto.setSerialNum("1234567890");
		machineDetailDto.setMacAddress("100.100.100.80");
		machineDetailDto.setLangCode("ENG");
		machineDetailDto.setIsActive(true);
		machineDetailDtoList.add(machineDetailDto);
		
		Machine machineDetail = new Machine();
		machineDetail.setId("1000");
		machineDetail.setName("HP");
		machineDetail.setSerialNum("1234567890");
		machineDetail.setMacAddress("100.100.100.80");
		machineDetail.setLangCode("ENG");
		machineDetail.setIsActive(true);

		List<Machine> machineDetailList = new ArrayList<Machine>();
		machineDetailList.add(machineDetail);
		Mockito.when(machineDetailsRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString())).thenReturn(machineDetailList);
		Mockito.when(objectMapperUtil.mapAll(machineDetailList, MachineDto.class))
				.thenReturn(machineDetailDtoList);
		MachineResponseDto actual = machineDetailServiceImpl.getMachineLangcode("ENG");

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineDetails().size() > 0);

	}
	
	@Test(expected = DataNotFoundException.class)
	public void getMachineDetailLangThrowsMachineNotFoundExcetionTest() {
		Mockito.when(machineDetailsRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG")).thenThrow(DataNotFoundException.class);
		machineDetailServiceImpl.getMachineLangcode("ENG");

	}

	@Test(expected = MasterDataServiceException.class)
	public void getMachineDetailLangThrowsDataAccessExcetionTest() {
		Mockito.when(machineDetailsRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG")).thenThrow(DataRetrievalFailureException.class);
		machineDetailServiceImpl.getMachineLangcode("ENG");

	}
	
	
	@Test(expected = DataNotFoundException.class)
	public void getMachineDetailThrowsMachineDetailNotFoundExceptionTest() {
		doReturn(null).when(machineDetailsRepository).findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull("ENG");
		machineDetailServiceImpl.getMachineLangcode("ENG");

	}

}


