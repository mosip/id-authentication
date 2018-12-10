package io.mosip.kernel.idgenerator.tsp.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;
import io.mosip.kernel.idgenerator.tsp.entity.Tsp;
import io.mosip.kernel.idgenerator.tsp.exception.TspIdServiceException;
import io.mosip.kernel.idgenerator.tsp.repository.TspRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TspIdServiceTest {

	@Autowired
	TspIdGenerator<TspResponseDTO> service;

	@MockBean
	TspRepository tspRepository;

	@Test
	public void generateNewIdTest() {
		Tsp entity = new Tsp();
		entity.setTspId(1000);
		when(tspRepository.findMaxTspId()).thenReturn(null);
		when(tspRepository.save(Mockito.any())).thenReturn(entity);
		assertThat(service.generateId().getTspId(), is(1000L));
	}

	@Test
	public void generateIdTest() {
		Tsp entity = new Tsp();
		entity.setTspId(1000);
		Tsp entityResponse = new Tsp();
		entityResponse.setTspId(1001);
		when(tspRepository.findMaxTspId()).thenReturn(entity);
		when(tspRepository.save(Mockito.any())).thenReturn(entityResponse);
		assertThat(service.generateId().getTspId(), is(1001L));
	}

	@Test(expected = TspIdServiceException.class)
	public void generateIdFetchExceptionTest() {
		when(tspRepository.findMaxTspId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateId();
	}

	@Test(expected = TspIdServiceException.class)
	public void generateIdInsertExceptionTest() {
		when(tspRepository.findMaxTspId()).thenReturn(null);
		when(tspRepository.save(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateId();
	}
}
