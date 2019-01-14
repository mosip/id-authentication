package io.mosip.kernel.idgenerator.registrationcenterid.test.service;

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
import io.mosip.kernel.core.idgenerator.spi.RegistrationCenterIdGenerator;
import io.mosip.kernel.idgenerator.registrationcenterid.entity.RegistrationCenterId;
import io.mosip.kernel.idgenerator.registrationcenterid.exception.RegistrationCenterIdServiceException;
import io.mosip.kernel.idgenerator.registrationcenterid.repository.RegistrationCenterIdRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RegistrationCenterIdServiceTest {
	@Autowired
	RegistrationCenterIdGenerator<Integer> service;

	@MockBean
	RegistrationCenterIdRepository repository;

	@Test
	public void generateRegistrationCenterIdTest() {
		RegistrationCenterId entity = new RegistrationCenterId();
		entity.setRcid(1000);
		when(repository.findMaxRegistrationCenterId()).thenReturn(null);
		when(repository.save(Mockito.any())).thenReturn(entity);
		assertThat(service.generateRegistrationCenterId(), is(1000));
	}

	@Test
	public void generateRegCenterIdTest() {
		RegistrationCenterId entity = new RegistrationCenterId();
		entity.setRcid(1000);
		RegistrationCenterId entityResponse = new RegistrationCenterId();
		entityResponse.setRcid(1001);
		when(repository.findMaxRegistrationCenterId()).thenReturn(entity);
		when(repository.save(Mockito.any())).thenReturn(entityResponse);
		assertThat(service.generateRegistrationCenterId(), is(1001));
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void generateIdFetchExceptionTest() {
		when(repository.findMaxRegistrationCenterId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void generateIdInsertExceptionTest() {
		when(repository.findMaxRegistrationCenterId()).thenReturn(null);
		when(repository.save(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void idServiceFetchExceptionTest() throws Exception {

		when(repository.findMaxRegistrationCenterId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void idServiceInsertExceptionTest() throws Exception {
		when(repository.save(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

}
