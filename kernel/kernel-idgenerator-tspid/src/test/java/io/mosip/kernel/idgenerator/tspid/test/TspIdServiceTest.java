package io.mosip.kernel.idgenerator.tspid.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tspid.entity.Tsp;
import io.mosip.kernel.idgenerator.tspid.exception.TspIdException;
import io.mosip.kernel.idgenerator.tspid.repository.TspRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TspIdServiceTest {

	@Value("${mosip.kernel.tspid.test.valid-new-tspid}")
	private String newTspId;
	
	@Autowired
	TspIdGenerator<String> service;

	@MockBean
	TspRepository tspRepository;

	//@Test
	public void generateNewIdTest() {
		Tsp entity = new Tsp();
		entity.setTspId(1000);
		when(tspRepository.findLastTspId()).thenReturn(null);
		when(tspRepository.save(Mockito.any())).thenReturn(entity);
		assertThat(service.generateId(), is("1000"));
	}

	@Test
	public void generateIdTest() {
		Tsp entity = new Tsp();
		entity.setTspId(1000);
		when(tspRepository.findLastTspId()).thenReturn(entity);
		when(tspRepository.updateTspId(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(1);
		assertThat(service.generateId(), is(newTspId));
	}

	@Test(expected = TspIdException.class)
	public void generateIdFetchExceptionTest() {
		when(tspRepository.findLastTspId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateId();
	}

	@Test(expected = TspIdException.class)
	public void generateIdInsertExceptionTest() {
		when(tspRepository.findLastTspId()).thenReturn(null);
		when(tspRepository.save(Mockito.any())).thenThrow(new TspIdException("", "cannot execute statement", null));
		service.generateId();
	}

	@Test(expected = TspIdException.class)
	public void tspIdServiceFetchExceptionTest() throws Exception {

		when(tspRepository.findLastTspId()).thenThrow(new TspIdException("", "cannot execute statement", null));
		service.generateId();
	}

	@Test(expected = TspIdException.class)
	public void tspIdServiceInsertExceptionTest() throws Exception {
		when(tspRepository.save(Mockito.any())).thenThrow(new TspIdException("", "cannot execute statement", null));
		service.generateId();
	}

	@Test(expected = TspIdException.class)
	public void tspIdServiceExceptionTest() throws Exception {
		Tsp entity = new Tsp();
		entity.setTspId(1000);
		when(tspRepository.findLastTspId()).thenReturn(entity);
		when(tspRepository.updateTspId(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateId();
	}
}
