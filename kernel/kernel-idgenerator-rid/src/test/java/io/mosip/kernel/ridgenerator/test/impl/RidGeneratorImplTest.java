package io.mosip.kernel.ridgenerator.test.impl;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.ridgenerator.entity.RidEntity;
import io.mosip.kernel.ridgenerator.exception.MosipEmptyInputException;
import io.mosip.kernel.ridgenerator.exception.MosipInputLengthException;
import io.mosip.kernel.ridgenerator.exception.MosipNullValueException;
import io.mosip.kernel.ridgenerator.impl.RidGeneratorImpl;
import io.mosip.kernel.ridgenerator.repository.RidRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RidGeneratorImplTest {

	@Mock
	RidRepository repository;

	@InjectMocks
	RidGeneratorImpl impl;

	@Test
	public void generateIdTypeTest() {
		RidEntity entity = new RidEntity();
		entity.setDongleId("23432");
		entity.setSequenceId(00001);
		when(repository.findById(RidEntity.class, "23432")).thenReturn(entity);
		assertThat(impl.generateId("1234", "23432"), isA(String.class));
	}

	@Test(expected = MosipNullValueException.class)
	public void centerIdNullExceptionTest() {
		impl.generateId(null, "23432");
	}

	@Test(expected = MosipNullValueException.class)
	public void doungleIdNullExceptionTest() {
		impl.generateId("1234", null);
	}

	@Test(expected = MosipEmptyInputException.class)
	public void centerIdEmptyExceptionTest() {
		impl.generateId("", "23432");
	}

	@Test(expected = MosipEmptyInputException.class)
	public void dongleIdEmptyExceptionTest() {
		impl.generateId("1234", "");
	}

	@Test(expected = MosipInputLengthException.class)
	public void centreIdLengthTest() {
		impl.generateId("123", "23456");
	}

	@Test(expected = MosipInputLengthException.class)
	public void dongleIdLengthTest() {
		impl.generateId("1234", "23");
	}

	@Test
	public void generateIdFirstSequenceTypeTest() {
		when(repository.findById(RidEntity.class, "23432")).thenReturn(null);
		assertThat(impl.generateId("1234", "23432"), isA(String.class));
	}

	@Test
	public void generateIdMaxSequenceTypeTest() {
		RidEntity entity = new RidEntity();
		entity.setDongleId("23432");
		entity.setSequenceId(99999);
		when(repository.findById(RidEntity.class, "23432")).thenReturn(entity);
		assertThat(impl.generateId("1234", "23432"), isA(String.class));
	}

}
