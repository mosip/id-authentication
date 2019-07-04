package io.mosip.kernel.ridgenerator.test.service;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.ridgenerator.dto.RidGeneratorResponseDto;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.exception.EmptyInputException;
import io.mosip.kernel.ridgenerator.exception.InputLengthException;
import io.mosip.kernel.ridgenerator.exception.RidException;
import io.mosip.kernel.ridgenerator.repository.RidRepository;
import io.mosip.kernel.ridgenerator.service.RidGeneratorService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidGeneratorServiceTest {

	@Autowired
	private RidGeneratorService<RidGeneratorResponseDto> service;

	@MockBean
	private RidRepository repository;

	Rid entity = null;

	@Before
	public void setUp() {
		entity = new Rid();
		entity.setCenterId("23123");
		entity.setMachineId("67687");
		entity.setCurrentSequenceNo(1);
		entity.setCreatedBy("SYSTEM");
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
	}

	@Test
	public void generateRidFromExistingSequenceTest() {
		when(repository.findRid(Mockito.any(), Mockito.any())).thenReturn(entity);
		assertThat(service.generateRid("23123", "67687"), isA(RidGeneratorResponseDto.class));
	}

	@Test
	public void generateNewRidTest() {
		when(repository.findRid(Mockito.any(), Mockito.any())).thenReturn(null);
		assertThat(service.generateRid("23123", "67687"), isA(RidGeneratorResponseDto.class));
	}

	@Test(expected = RidException.class)
	public void generateRidFetchExceptionTest() {
		when(repository.findRid(Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		service.generateRid("23123", "67687");
	}

	@Test(expected = RidException.class)
	public void generateRidUpdateExceptionTest() {
		when(repository.findRid(Mockito.any(), Mockito.any())).thenReturn(entity);
		when(repository.updateRid(Mockito.anyInt(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("errorCode", "errorMessage", null));
		service.generateRid("23123", "67687");
	}

	@Test(expected = InputLengthException.class)
	public void generateRidInvalidCenterIdLengthExceptionTest() {
		service.generateRid("1", "67687");
	}

	@Test(expected = InputLengthException.class)
	public void generateRidInvalidMachineIdLengthExceptionTest() {
		service.generateRid("23123", "6");
	}

	@Test(expected = EmptyInputException.class)
	public void generateRidEmptyLengthCenterIdExceptionTest() {
		service.generateRid("", "67687");
	}

	@Test(expected = EmptyInputException.class)
	public void generateRidEmptyLengthMachineIdExceptionTest() {
		service.generateRid("23123", "");
	}

	@Test
	public void generateRidMaximumSequenceTest() {
		entity.setCurrentSequenceNo(99999);
		when(repository.findRid(Mockito.any(), Mockito.any())).thenReturn(entity);
		assertThat(service.generateRid("23123", "67687"), isA(RidGeneratorResponseDto.class));
	}
}
