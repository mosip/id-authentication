package io.mosip.registration.processor.status.dao;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
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

import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;

/**
 * The Class SyncRegistrationDaoTest.
 * 
 * @author M1047487
 */
@RunWith(MockitoJUnitRunner.class)
public class SyncRegistrationDaoTest {

	/** The sync registration dao. */
	@InjectMocks
	SyncRegistrationDao syncRegistrationDao;

	/** The sync registration repository. */
	@Mock
	RegistrationRepositary<SyncRegistrationEntity, String> syncRegistrationRepository;

	/** The sync registration entity. */
	private SyncRegistrationEntity syncRegistrationEntity;

	private List<SyncRegistrationEntity> syncRegistrationEntityList;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {

		syncRegistrationEntityList = new ArrayList<>();

		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setId("0c326dc2-ac54-4c2a-98b4-b0c620f1661f");
		syncRegistrationEntity.setRegistrationId("1001");
		syncRegistrationEntity.setRegistrationType(SyncTypeDto.NEW.toString());
		syncRegistrationEntity.setParentRegistrationId("1234");
		syncRegistrationEntity.setStatusCode(SyncStatusDto.PRE_SYNC.toString());
		syncRegistrationEntity.setStatusComment("NEW");
		syncRegistrationEntity.setLangCode("eng");
		syncRegistrationEntity.setIsActive(true);
		syncRegistrationEntity.setIsDeleted(false);
		syncRegistrationEntity.setCreateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setUpdateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setCreatedBy("MOSIP");
		syncRegistrationEntity.setUpdatedBy("MOSIP");

		syncRegistrationEntityList.add(syncRegistrationEntity);

		Mockito.when(syncRegistrationRepository.save(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);

		Mockito.when(syncRegistrationRepository.createQuerySelect(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(syncRegistrationEntityList);

	}

	/**
	 * Save success test.
	 */
	@Test
	public void saveTest() {
		SyncRegistrationEntity syncRegistrationEntityResult = syncRegistrationDao.save(syncRegistrationEntity);
		assertEquals("Verifing Registration Id after saving in DB. Expected value is 1001",
				syncRegistrationEntity.getId(), syncRegistrationEntityResult.getId());
	}

	@Test
	public void updateTest() {
		SyncRegistrationEntity syncRegistrationEntityResult = syncRegistrationDao.update(syncRegistrationEntity);
		assertEquals("Verifing Registration Id after Updating in DB. Expected value is 1001",
				syncRegistrationEntity.getId(), syncRegistrationEntityResult.getId());
	}

	/**
	 * Find by id success test.
	 */
	@Test
	public void findByIdSuccessTest() {
		SyncRegistrationEntity syncRegistrationEntityResult = syncRegistrationDao.findById("1001");
		assertEquals("Check id Registration Id is present in DB, expected valie is 1001",
				syncRegistrationEntity.getRegistrationId(), syncRegistrationEntityResult.getRegistrationId());
	}

	@Test
	public void findByIdFailureTest() {
		syncRegistrationEntityList = new ArrayList<>();
		Mockito.when(syncRegistrationRepository.createQuerySelect(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(syncRegistrationEntityList);
		SyncRegistrationEntity syncRegistrationEntityResult = syncRegistrationDao.findById("1001");
		assertEquals("Check id Registration Id is present in DB, expected value is empty List", null,
				syncRegistrationEntityResult);
	}

}
