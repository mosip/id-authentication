package io.mosip.kernel.masterdata.test.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataRetrievalFailureException;

import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.exception.MachineHistoryFetchException;
import io.mosip.kernel.masterdata.exception.MachineHistoryMappingException;
import io.mosip.kernel.masterdata.exception.MachineHistroyNotFoundException;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.service.impl.MachineHistoryServiceImpl;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;
import io.mosip.kernel.masterdata.utils.StringToLocalDateTimeConverter;

@RunWith(MockitoJUnitRunner.class)
public class MachineHistoryServiceImplTest {

	@InjectMocks
	private MachineHistoryServiceImpl machineHistoryServiceImpl;

	@Mock
	private MachineHistoryRepository machineHistoryRepository;

	@Mock
	private ObjectMapperUtil objMapper;

	@Mock
	private StringToLocalDateTimeConverter stringToLocalDateTimeConverter;

	@Before
	public void setUp() {
		machineHistoryServiceImpl = new MachineHistoryServiceImpl();
		MockitoAnnotations.initMocks(this);
	}

	public LocalDateTime localDateTime = LocalDateTime.of(2016, 12, 01, 23, 12, 56);
	public List<MachineHistory> machineHistoryList = new ArrayList<MachineHistory>();

	@Test
	public void testGetMachineHistoryIdLangEffectDtimes() throws InterruptedException {
		List<MachineHistoryDto> machineHistoryDtoList = new ArrayList<MachineHistoryDto>();
		MachineHistoryDto machineHistoryDto = new MachineHistoryDto();
		machineHistoryDto.setId("1000");
		machineHistoryDto.setName("HP");
		machineHistoryDto.setSerialNum("1234567890");
		machineHistoryDto.setMacAddress("100.100.100.80");
		machineHistoryDto.setLangCode("ENG");
		machineHistoryDto.setIsActive(true);
		machineHistoryDto.setCreatedBy("Admin");
		machineHistoryDto.setCreatedtime(localDateTime);
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
		machineHistory.setCreatedtimes(localDateTime);
		machineHistory.setUpdatedBy("Admin");
		machineHistory.setIsDeleted(false);
		machineHistory.setIpAddress("100.10.01.01");
		machineHistory.setMspecId("12345678");

		List<MachineHistory> machineHistoryList = new ArrayList<MachineHistory>();
		machineHistoryList.add(machineHistory);
		Mockito.when(machineHistoryRepository.findByIdAndLangCodeAndEffectDtimesLessThanEqual(Mockito.anyString(),
				Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenReturn(machineHistoryList);
		Mockito.when(stringToLocalDateTimeConverter.convert(Mockito.anyString())).thenReturn(localDateTime);
		Mockito.when(objMapper.mapAll(machineHistoryList, MachineHistoryDto.class)).thenReturn(machineHistoryDtoList);
		List<MachineHistoryDto> actual = machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG",
				"2018-10-29T00:00:05");
		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.size() > 0);

	}

	@Test(expected = MachineHistroyNotFoundException.class)
	public void testGetMachineHistoryIdLangThrowsExcetion() {
		Mockito.when(stringToLocalDateTimeConverter.convert(Mockito.anyString())).thenReturn(localDateTime);
		Mockito.when(machineHistoryRepository.findByIdAndLangCodeAndEffectDtimesLessThanEqual(Mockito.anyString(),
				Mockito.anyString(), Mockito.any())).thenReturn(null);

		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

	@Test(expected = MachineHistoryFetchException.class)
	public void testGetMachineHistoryIdLangThrowsDataAccessExcetion() {
		Mockito.when(stringToLocalDateTimeConverter.convert(Mockito.anyString())).thenReturn(localDateTime);
		Mockito.when(machineHistoryRepository.findByIdAndLangCodeAndEffectDtimesLessThanEqual(Mockito.anyString(),
				Mockito.anyString(), Mockito.any(LocalDateTime.class))).thenThrow(DataRetrievalFailureException.class);
		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

	@Test(expected = MachineHistoryMappingException.class)
	public void testGetMachineHistoryIdLangThrowsIllegalArgumentExcetion() {
		Mockito.when(objMapper.mapAll(machineHistoryList, MachineHistoryDto.class))
				.thenThrow(IllegalArgumentException.class);
		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

	@Test(expected = MachineHistoryMappingException.class)
	public void testGetMachineHistoryIdLangThrowsConfigurationExcetion() {
		Mockito.when(objMapper.mapAll(machineHistoryList, MachineHistoryDto.class))
				.thenThrow(ConfigurationException.class);
		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

	@Test(expected = MachineHistoryMappingException.class)
	public void testGetMachineHistoryIdLangThrowsMappingExcetion() {
		Mockito.when(objMapper.mapAll(machineHistoryList, MachineHistoryDto.class)).thenThrow(MappingException.class);
		machineHistoryServiceImpl.getMachineHistroyIdLangEffDTime("1000", "ENG", "2018-10-29T00:00:05");

	}

}
