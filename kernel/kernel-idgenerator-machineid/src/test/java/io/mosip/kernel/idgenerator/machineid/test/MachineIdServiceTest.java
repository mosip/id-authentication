package io.mosip.kernel.idgenerator.machineid.test;

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
import io.mosip.kernel.core.idgenerator.spi.MachineIdGenerator;
import io.mosip.kernel.idgenerator.machineid.entity.MachineId;
import io.mosip.kernel.idgenerator.machineid.exception.MachineIdServiceException;
import io.mosip.kernel.idgenerator.machineid.repository.MachineIdRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MachineIdServiceTest {
	@Autowired
	MachineIdGenerator<String> service;

	@MockBean
	MachineIdRepository repository;

	@Test
	public void generateMachineIdTest() {
		MachineId entity = new MachineId();
		entity.setMId(1000);
		when(repository.findLastMID()).thenReturn(null);
		when(repository.save(Mockito.any())).thenReturn(entity);
		assertThat(service.generateMachineId(), is("1000"));
	}

	@Test
	public void generateRegCenterIdTest() {
		MachineId entity = new MachineId();
		entity.setMId(1000);
		MachineId entityResponse = new MachineId();
		entityResponse.setMId(1001);
		when(repository.findLastMID()).thenReturn(entity);
		when(repository.save(Mockito.any())).thenReturn(entityResponse);
		assertThat(service.generateMachineId(), is("1001"));
	}

	@Test(expected = MachineIdServiceException.class)
	public void generateIdFetchExceptionTest() {
		when(repository.findLastMID()).thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateMachineId();
	}

	@Test(expected = MachineIdServiceException.class)
	public void generateIdInsertExceptionTest() {
		when(repository.findLastMID()).thenReturn(null);
		when(repository.save(Mockito.any()))
				.thenThrow(new MachineIdServiceException("", "cannot execute statement", null));
		service.generateMachineId();
	}

	@Test(expected = MachineIdServiceException.class)
	public void idServiceFetchExceptionTest() throws Exception {

		when(repository.findLastMID()).thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateMachineId();
	}

	@Test(expected = MachineIdServiceException.class)
	public void idServiceInsertExceptionTest() throws Exception {
		when(repository.save(Mockito.any()))
				.thenThrow(new MachineIdServiceException("", "cannot execute statement", null));
		service.generateMachineId();
	}

	@Test(expected = MachineIdServiceException.class)
	public void machineIdServiceExceptionTest() throws Exception {
		MachineId entity = new MachineId();
		entity.setMId(1000);
		when(repository.findLastMID()).thenReturn(entity);
		when(repository.updateMID(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		service.generateMachineId();
	}

}
