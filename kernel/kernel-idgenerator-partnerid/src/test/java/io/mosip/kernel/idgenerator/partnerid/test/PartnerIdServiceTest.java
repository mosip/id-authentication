package io.mosip.kernel.idgenerator.partnerid.test;

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
import io.mosip.kernel.core.idgenerator.spi.PartnerIdGenerator;
import io.mosip.kernel.idgenerator.partnerid.entity.Partner;
import io.mosip.kernel.idgenerator.partnerid.excepion.PartnerIdException;
import io.mosip.kernel.idgenerator.partnerid.repository.PartnerRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PartnerIdServiceTest {

	@Value("${mosip.kernel.partnerid.test.valid-initial-partnerid}")
	private int initialPartnerId;

	@Value("${mosip.kernel.partnerid.test.valid-new-partnerid}")
	private int newPartnerId;

	@Autowired
	PartnerIdGenerator<String> service;

	@MockBean
	PartnerRepository partnerRepository;

	@Test
	public void generateNewIdTest() {
		Partner entity = new Partner();
		entity.setTspId(initialPartnerId);
		when(partnerRepository.findLastTspId()).thenReturn(null);
		when(partnerRepository.create(Mockito.any())).thenReturn(entity);
		assertThat(service.generateId(), is(Integer.toString(initialPartnerId)));
	}

	@Test
	public void generateIdTest() {
		Partner entity = new Partner();
		entity.setTspId(initialPartnerId);
		when(partnerRepository.findLastTspId()).thenReturn(entity);
		when(partnerRepository.create(Mockito.any())).thenReturn(entity);
		assertThat(service.generateId(), is(Integer.toString(newPartnerId)));
	}

	@Test(expected = PartnerIdException.class)
	public void generateIdFetchExceptionTest() {
		when(partnerRepository.findLastTspId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = PartnerIdException.class)
	public void generateIdInsertExceptionTest() {
		when(partnerRepository.findLastTspId()).thenReturn(null);
		when(partnerRepository.create(Mockito.any()))
				.thenThrow(new PartnerIdException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = PartnerIdException.class)
	public void partnerIdServiceFetchExceptionTest() throws Exception {

		when(partnerRepository.findLastTspId())
				.thenThrow(new PartnerIdException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = PartnerIdException.class)
	public void partnerIdServiceInsertExceptionTest() throws Exception {
		when(partnerRepository.create(Mockito.any()))
				.thenThrow(new PartnerIdException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}

	@Test(expected = PartnerIdException.class)
	public void partnerIdServiceExceptionTest() throws Exception {
		Partner entity = new Partner();
		entity.setTspId(1000);
		when(partnerRepository.findLastTspId()).thenReturn(entity);
		when(partnerRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", new RuntimeException()));
		service.generateId();
	}
}
