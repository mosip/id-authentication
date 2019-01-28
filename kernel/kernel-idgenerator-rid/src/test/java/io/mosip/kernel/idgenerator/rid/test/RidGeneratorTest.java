package io.mosip.kernel.idgenerator.rid.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.idgenerator.rid.entity.Rid;
import io.mosip.kernel.idgenerator.rid.exception.EmptyInputException;
import io.mosip.kernel.idgenerator.rid.exception.InputLengthException;
import io.mosip.kernel.idgenerator.rid.exception.NullValueException;
import io.mosip.kernel.idgenerator.rid.exception.RidException;
import io.mosip.kernel.idgenerator.rid.repository.RidRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidGeneratorTest {

	@MockBean
	RidRepository repository;

	@Autowired
	RidGenerator<String> ridGeneratorImpl;

	//@Test
	public void generateIdTypeTest() {
		Rid entity = new Rid();
		entity.setCurrentSequenceNo(00001);
		when(repository.findLastRid()).thenReturn(entity);
		assertThat(ridGeneratorImpl.generateId("12345", "23432"), isA(String.class));
	}

	@Test(expected = NullValueException.class)
	public void centerIdNullExceptionTest() {
		ridGeneratorImpl.generateId(null, "23432");
	}

	@Test(expected = NullValueException.class)
	public void dongleIdNullExceptionTest() {
		ridGeneratorImpl.generateId("1234", null);
	}

	@Test(expected = EmptyInputException.class)
	public void centerIdEmptyExceptionTest() {
		ridGeneratorImpl.generateId("", "23432");
	}

	@Test(expected = EmptyInputException.class)
	public void dongleIdEmptyExceptionTest() {
		ridGeneratorImpl.generateId("1234", "");
	}

	@Test(expected = InputLengthException.class)
	public void centreIdLengthTest() {
		ridGeneratorImpl.generateId("123", "23456");
	}

	@Test(expected = InputLengthException.class)
	public void dongleIdLengthTest() {
		ridGeneratorImpl.generateId("1234", "23");
	}

	@Test
	public void generateIdFirstSequenceTypeTest() {
		when(repository.findLastRid()).thenReturn(null);
		assertThat(ridGeneratorImpl.generateId("12345", "23432"), isA(String.class));
	}

	@Test
	public void generateIdMaxSequenceTypeTest() {
		Rid entity = new Rid();
		entity.setCurrentSequenceNo(99999);
		when(repository.findLastRid()).thenReturn(entity);
		assertThat(ridGeneratorImpl.generateId("12345", "23432"), isA(String.class));
	}

	@Test
	public void generateIdTest() {
		Rid entity = new Rid();
		entity.setCurrentSequenceNo(00001);
		when(repository.findLastRid()).thenReturn(entity);
		assertThat(ridGeneratorImpl.generateId("1234", "23432", 4, 5), isA(String.class));
	}

	@Test(expected = RidException.class)
	public void generateIdFetchExceptionTest() {
		Rid entity = new Rid();
		entity.setCurrentSequenceNo(00001);
		when(repository.findLastRid()).thenThrow(DataRetrievalFailureException.class);
		ridGeneratorImpl.generateId("1234", "23432", 4, 5);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RidException.class)
	public void generateIdUpdateExceptionTest() {
		Rid entity = new Rid();
		entity.setCurrentSequenceNo(00001);
		when(repository.save(entity)).thenThrow(DataRetrievalFailureException.class, DataAccessLayerException.class);
		ridGeneratorImpl.generateId("1234", "23432", 4, 5);
	}

	@Test(expected = InputLengthException.class)
	public void generateIdInvalidCenterIdLengthTest() {
		Rid entity = new Rid();
		entity.setCurrentSequenceNo(00001);
		when(repository.findLastRid()).thenReturn(entity);
		assertThat(ridGeneratorImpl.generateId("1234", "23432", 0, 5), isA(String.class));
	}

}
