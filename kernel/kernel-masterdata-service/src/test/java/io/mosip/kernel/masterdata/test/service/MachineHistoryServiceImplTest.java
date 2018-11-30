
package io.mosip.kernel.masterdata.test.service;

import java.time.LocalDateTime;
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

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.service.impl.MachineHistoryServiceImpl;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@RunWith(MockitoJUnitRunner.class)
public class MachineHistoryServiceImplTest {

	@InjectMocks
	private MachineHistoryServiceImpl machineHistoryServiceImpl;

	@Mock
	private MachineHistoryRepository machineHistoryRepository;

	@Mock
	private MapperUtils objMapper;

	@Before
	public void setUp() {
		machineHistoryServiceImpl = new MachineHistoryServiceImpl();
		MockitoAnnotations.initMocks(this);
	}

	public LocalDateTime localDateTime = DateUtils.getUTCCurrentDateTime();
	public List<MachineHistory> machineHistoryList = new ArrayList<MachineHistory>();

	// @Test
	public void getMachineHistoryIdLangEffectDtimesTest() throws InterruptedException {
		MachineHistoryResponseDto machineHistoryResponseDto = new MachineHistoryResponseDto();
		List<MachineHistoryDto> machineHistoryDtoList = new ArrayList<MachineHistoryDto>();
		MachineHistoryDto machineHistoryDto = new MachineHistoryDto();
		machineHistoryDto.setId("1000");
		machineHistoryDto.setName("HP");
		machineHistoryDto.setSerialNum("1234567890");
		machineHistoryDto.setMacAddress("100.100.100.80");
		machineHistoryDto.setLangCode("ENG");
		machineHistoryDto.setIsActive(true);
		machineHistoryDto.setCreatedBy("Admin");
		machineHistoryDto.setCreatedtimes(localDateTime);
		machineHistoryDto.setUpdatedBy("Admin");
		machineHistoryDto.setIsDeleted(false);
		machineHistoryDto.setIpAddress("100.10.01.01");
		machineHistoryDto.setMspecId("12345678");

		machineHistoryDtoList.add(machineHistoryDto);
		MachineHistory machineHistory = new MachineHistory();
		machineHistory.setId("1000");
		machineHistory.setName("HP");
		machineHistory.setSerialNum("1234567890");
		machineHistory.setMacAddress("100.100.100.80");
		machineHistory.setLangCode("ENG");
		machineHistory.setIsActive(true);
		machineHistory.setCreatedBy("Admin");
		machineHistory.setCreatedDateTime(localDateTime);
		machineHistory.setUpdatedBy("Admin");
		machineHistory.setIsDeleted(false);
		machineHistory.setIpAddress("100.10.01.01");
		machineHistory.setMspecId("12345678");

		List<MachineHistory> machineHistoryList = new ArrayList<MachineHistory>();
		machineHistoryList.add(machineHistory);
		machineHistoryResponseDto.setMachineHistoryDetails(machineHistoryDtoList);
		Mockito.when(machineHistoryRepository.findByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalse(
				Mockito.anyString(), Mockito.anyString(), Mockito.any(LocalDateTime.class)))
				.thenReturn(machineHistoryList);
		Mockito.when(objMapper.mapAll(machineHistoryList, MachineHistoryDto.class)).thenReturn(machineHistoryDtoList);
		MachineHistoryResponseDto actual = machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG",
				"2018-10-29T00:00:05");
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.getMachineHistoryDetails().size() > 0);

	}

	@Test(expected = MasterDataServiceException.class)
	public void getMachineHistoryIdLangThrowsExcetionTest() {
		// Mockito.when(stringToLocalDateTimeConverter.convert(Mockito.anyString())).thenReturn(localDateTime);
		// Mockito.when(machineHistoryRepository
		// .findByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalse(Mockito.anyString(),
		// Mockito.anyString(), Mockito.any()))
		// .thenReturn(null);

		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

	@Test(expected = MasterDataServiceException.class)
	public void getMachineHistoryIdLangThrowsDataAccessExcetionTest() {
		// Mockito.when(stringToLocalDateTimeConverter.convert(Mockito.anyString())).thenReturn(localDateTime);
		// Mockito.when(machineHistoryRepository
		// .findByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalse(Mockito.anyString(),
		// Mockito.anyString(), Mockito.any(LocalDateTime.class)))
		// .thenThrow(DataRetrievalFailureException.class);
		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

}
