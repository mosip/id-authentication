package io.mosip.registration.processor.status.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.repositary.SyncRegistrationRepository;


/**
 * The Class SyncRegistrationDaoTest.
 * 
 *  @author M1047487
 */
@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@TestPropertySource({ "classpath:status-application.properties" })
@ContextConfiguration
public class SyncRegistrationDaoTest {

	/** The sync registration dao. */
	@InjectMocks
	SyncRegistrationDao syncRegistrationDao;

	/** The sync registration repository. */
	@Mock
	SyncRegistrationRepository syncRegistrationRepository;

	/** The sync registration entity. */
	private SyncRegistrationEntity syncRegistrationEntity;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {

		List<SyncRegistrationEntity> syncRegistrationEntityList = new ArrayList<>();

		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId("1001");
		syncRegistrationEntity.setParentRegistrationId("1002");
		syncRegistrationEntity.setIsActive(true);

		syncRegistrationEntityList.add(syncRegistrationEntity);

		Mockito.when(syncRegistrationRepository.save(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);

		Mockito.when(syncRegistrationRepository.createQuerySelect(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(syncRegistrationEntityList);

	}

	/**
	 * Save success test.
	 */
	@Test
	public void saveSuccessTest() {
		SyncRegistrationEntity syncRegistrationEntityResult = syncRegistrationDao.save(syncRegistrationEntity);
		assertEquals(syncRegistrationEntity.getSyncRegistrationId(),
				syncRegistrationEntityResult.getSyncRegistrationId());
	}

	/**
	 * Find by id success test.
	 */
	@Test
	public void findByIdSuccessTest() {
		SyncRegistrationEntity syncRegistrationEntityResult = syncRegistrationDao.findById("1001");
		assertEquals("1001", syncRegistrationEntityResult.getRegistrationId());
	}

}
