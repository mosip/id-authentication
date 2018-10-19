package io.mosip.registration.processor.status.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class RegistrationStatusDaoTest {

	private RegistrationStatusEntity registrationStatusEntity;
	private List<RegistrationStatusEntity> list;

	@InjectMocks
	RegistrationStatusDao registrationStatusDao = new RegistrationStatusDao();
	@Mock
	RegistrationRepositary<RegistrationStatusEntity, String> registrationStatusRepositary;

	@Before
	public void setup() {
		registrationStatusEntity = new RegistrationStatusEntity();
		registrationStatusEntity.setIsActive(true);

		list = new ArrayList<>();
		list.add(registrationStatusEntity);
		Mockito.when(registrationStatusRepositary.createQuerySelect(Matchers.any(), Matchers.any())).thenReturn(list);
		Mockito.when(registrationStatusRepositary.save(Matchers.any())).thenReturn(registrationStatusEntity);
	}

	@Test
	public void findbyfilesByThresholdTest() {
		List<RegistrationStatusEntity> rEntityList = registrationStatusDao
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString(), 48);
		assertEquals(list, rEntityList);
	}

	@Test
	public void getEnrolmentStatusByStatusCodeTest() {
		List<RegistrationStatusEntity> rEntityList = registrationStatusDao
				.getEnrolmentStatusByStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());
		assertEquals(list, rEntityList);
	}

	@Test
	public void getByIds() {
		List<String> idList = new ArrayList<>();
		idList.add("1000.zip");
		List<RegistrationStatusEntity> rEntityList = registrationStatusDao.getByIds(idList);
		assertEquals(list, rEntityList);
	}

	@Test
	public void findByIdTest() {
		RegistrationStatusEntity rEntity = registrationStatusDao.findById("1000.zip");
		assertEquals(registrationStatusEntity, rEntity);

	}

	@Test
	public void save() {
		RegistrationStatusEntity rEntity = registrationStatusDao.save(registrationStatusEntity);
		assertEquals(registrationStatusEntity, rEntity);

	}

	@Test
	public void update() {
		RegistrationStatusEntity rEntity = registrationStatusDao.update(registrationStatusEntity);
		assertEquals(registrationStatusEntity, rEntity);

	}
}
