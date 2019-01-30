package io.mosip.kernel.idgenerator.regcenterid.test;

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
import io.mosip.kernel.core.idgenerator.spi.RegistrationCenterIdGenerator;
import io.mosip.kernel.idgenerator.regcenterid.entity.RegistrationCenterId;
import io.mosip.kernel.idgenerator.regcenterid.exception.RegistrationCenterIdServiceException;
import io.mosip.kernel.idgenerator.regcenterid.repository.RegistrationCenterIdRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RegistrationCenterIdServiceTest {

	@Value("${mosip.kernel.rcid.test.valid-initial-rcid}")
	private int initialRcid;
	
	@Value("${mosip.kernel.rcid.test.valid-new-rcid}")
	private int newRcid;
	
	@Autowired
	RegistrationCenterIdGenerator<String> service;

	@MockBean
	RegistrationCenterIdRepository repository;

	@Test
	public void generateRegistrationCenterIdTest() {
		RegistrationCenterId entity = new RegistrationCenterId();
		entity.setRcid(initialRcid);
		when(repository.findLastRCID()).thenReturn(null);
		when(repository.save(Mockito.any())).thenReturn(entity);
		assertThat(service.generateRegistrationCenterId(), is(Integer.toString(initialRcid)));
	}

	@Test
	public void generateRegCenterIdTest() {
		RegistrationCenterId entity = new RegistrationCenterId();
		entity.setRcid(initialRcid);
		RegistrationCenterId entityResponse = new RegistrationCenterId();
		entityResponse.setRcid(1001);
		when(repository.findLastRCID()).thenReturn(entity);
		when(repository.save(Mockito.any())).thenReturn(entityResponse);
		assertThat(service.generateRegistrationCenterId(), is(Integer.toString(newRcid)));
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void generateIdFetchExceptionTest() {
		when(repository.findLastRCID()).thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void generateIdInsertExceptionTest() {
		when(repository.findLastRCID()).thenReturn(null);
		when(repository.save(Mockito.any()))
				.thenThrow(new RegistrationCenterIdServiceException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void idServiceFetchExceptionTest() throws Exception {

		when(repository.findLastRCID()).thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void idServiceInsertExceptionTest() throws Exception {
		when(repository.save(Mockito.any()))
				.thenThrow(new RegistrationCenterIdServiceException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

	@Test(expected = RegistrationCenterIdServiceException.class)
	public void tspIdServiceExceptionTest() throws Exception {
		RegistrationCenterId entity = new RegistrationCenterId();
		entity.setRcid(1000);
		when(repository.findLastRCID()).thenReturn(entity);
		when(repository.updateRCID(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateRegistrationCenterId();
	}

}
