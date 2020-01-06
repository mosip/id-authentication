package io.mosip.kernel.idgenerator.mispid.test;

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
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.MispIdGenerator;
import io.mosip.kernel.idgenerator.mispid.entity.Misp;
import io.mosip.kernel.idgenerator.mispid.exception.MispIdException;
import io.mosip.kernel.idgenerator.mispid.repository.MispRepository;

/**
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * 
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MispIdServiceTest {

	@Value("${mosip.kernel.mispid.test.valid-initial-mispid}")
	private int initialMispid;

	@Value("${mosip.kernel.mispid.test.valid-new-mispid}")
	private int newMispId;

	@Autowired
	MispIdGenerator<String> service;

	@MockBean
	MispRepository mispRepository;
	
	@MockBean
	private RestTemplate restTemplate;

	@Test
	public void generateNewIdTest() {
		Misp entity = new Misp();
		entity.setMispId(initialMispid);
		when(mispRepository.findLastMispId()).thenReturn(null);
		when(mispRepository.create(Mockito.any())).thenReturn(entity);
		assertThat(service.generateId(), is(Integer.toString(initialMispid)));
	}

	@Test
	public void generateIdTest() {
		Misp entity = new Misp();
		entity.setMispId(initialMispid);
		when(mispRepository.findLastMispId()).thenReturn(entity);
		when(mispRepository.create(Mockito.any())).thenReturn(entity);
		assertThat(service.generateId(), is(Integer.toString(newMispId)));
	}

	@Test(expected = MispIdException.class)
	public void generateIdFetchExceptionTest() {
		when(mispRepository.findLastMispId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = MispIdException.class)
	public void generateIdInsertExceptionTest() {
		when(mispRepository.findLastMispId()).thenReturn(null);
		when(mispRepository.create(Mockito.any()))
				.thenThrow(new MispIdException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = MispIdException.class)
	public void mispIdServiceFetchExceptionTest() throws Exception {

		when(mispRepository.findLastMispId())
				.thenThrow(new MispIdException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = MispIdException.class)
	public void mispIdServiceInsertExceptionTest() throws Exception {
		when(mispRepository.create(Mockito.any()))
				.thenThrow(new MispIdException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = MispIdException.class)
	public void mispIdServiceExceptionTest() throws Exception {
		Misp entity = new Misp();
		entity.setMispId(1000);
		when(mispRepository.findLastMispId()).thenReturn(entity);
		when(mispRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}
}
