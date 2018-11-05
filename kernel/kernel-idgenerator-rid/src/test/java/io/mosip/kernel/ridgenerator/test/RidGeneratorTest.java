package io.mosip.kernel.ridgenerator.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.exception.EmptyInputException;
import io.mosip.kernel.ridgenerator.exception.InputLengthException;
import io.mosip.kernel.ridgenerator.exception.NullValueException;
import io.mosip.kernel.ridgenerator.impl.RidGeneratorImpl;
import io.mosip.kernel.ridgenerator.repository.RidRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidGeneratorTest {

	@Mock
	RidRepository repository;

	@InjectMocks
	RidGeneratorImpl impl;

	@Test
	public void generateIdTypeTest() {
		Rid entity = new Rid();
		entity.setDongleId("23432");
		entity.setSequenceId(00001);
		when(repository.findById(Rid.class, "23432")).thenReturn(entity);
		assertThat(impl.generateId("1234", "23432"), isA(String.class));
	}

	@Test(expected = NullValueException.class)
	public void centerIdNullExceptionTest() {
		impl.generateId(null, "23432");
	}

	@Test(expected = NullValueException.class)
	public void dongleIdNullExceptionTest() {
		impl.generateId("1234", null);
	}

	@Test(expected = EmptyInputException.class)
	public void centerIdEmptyExceptionTest() {
		impl.generateId("", "23432");
	}

	@Test(expected = EmptyInputException.class)
	public void dongleIdEmptyExceptionTest() {
		impl.generateId("1234", "");
	}

	@Test(expected = InputLengthException.class)
	public void centreIdLengthTest() {
		impl.generateId("123", "23456");
	}

	@Test(expected = InputLengthException.class)
	public void dongleIdLengthTest() {
		impl.generateId("1234", "23");
	}

	@Test
	public void generateIdFirstSequenceTypeTest() {
		when(repository.findById(Rid.class, "23432")).thenReturn(null);
		assertThat(impl.generateId("1234", "23432"), isA(String.class));
	}

	@Test
	public void generateIdMaxSequenceTypeTest() {
		Rid entity = new Rid();
		entity.setDongleId("23432");
		entity.setSequenceId(99999);
		when(repository.findById(Rid.class, "23432")).thenReturn(entity);
		assertThat(impl.generateId("1234", "23432"), isA(String.class));
	}

}
