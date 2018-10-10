package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.constant.HibernateErrorCodes;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.impl.SyncRegistrationServiceImpl;

/**
 * The Class SyncRegistrationServiceTest.
 * 
 * @author M1047487
 */
@RunWith(MockitoJUnitRunner.class)
public class SyncRegistrationServiceTest {

	/** The sync registration dto. */
	private SyncRegistrationDto syncRegistrationDto;

	/** The sync registration entity. */
	private SyncRegistrationEntity syncRegistrationEntity;

	/** The entities. */
	private List<SyncRegistrationDto> entities;

	/** The sync registration dao. */
	@Mock
	private SyncRegistrationDao syncRegistrationDao;

	/** The audit request builder. */
	@Mock
	private AuditRequestBuilder auditRequestBuilder;

	/** The audit handler. */
	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;

	/** The sync registration service. */
	@InjectMocks
	private SyncRegistrationService<SyncRegistrationDto> syncRegistrationService = new SyncRegistrationServiceImpl();

	/**
	 * Setup.
	 *
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		entities = new ArrayList<>();

		syncRegistrationDto = new SyncRegistrationDto();

		syncRegistrationDto.setRegistrationId("1002");
		syncRegistrationDto.setLangCode("eng");
		syncRegistrationDto.setIsActive(true);
		syncRegistrationDto.setIsDeleted(false);
		syncRegistrationDto.setParentRegistrationId("1234");
		syncRegistrationDto.setStatusComment("NEW");
		syncRegistrationDto.setSyncStatusDto(SyncStatusDto.INITIATED);
		syncRegistrationDto.setSyncTypeDto(SyncTypeDto.NEW_REGISTRATION);
		entities.add(syncRegistrationDto);

		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setSyncRegistrationId("0c326dc2-ac54-4c2a-98b4-b0c620f1661f");
		syncRegistrationEntity.setRegistrationId(syncRegistrationDto.getRegistrationId());
		syncRegistrationEntity.setRegistrationType(syncRegistrationDto.getSyncTypeDto().toString());
		syncRegistrationEntity.setParentRegistrationId(syncRegistrationDto.getParentRegistrationId());
		syncRegistrationEntity.setStatusCode(syncRegistrationDto.getSyncStatusDto().toString());
		syncRegistrationEntity.setStatusComment(syncRegistrationDto.getStatusComment());
		syncRegistrationEntity.setLangCode(syncRegistrationDto.getLangCode());
		syncRegistrationEntity.setIsActive(syncRegistrationDto.getIsActive());
		syncRegistrationEntity.setIsDeleted(syncRegistrationDto.getIsDeleted());
		syncRegistrationEntity.setCreateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setUpdateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setCreatedBy("MOSIP");
		syncRegistrationEntity.setUpdatedBy("MOSIP");

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		AuditRequestDto auditRequest1 = new AuditRequestDto();

		Field f = SyncRegistrationServiceImpl.class.getDeclaredField("auditRequestBuilder");
		f.setAccessible(true);
		f.set(syncRegistrationService, auditRequestBuilder);

		Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
		f1.setAccessible(true);
		f1.set(auditRequestBuilder, auditRequest1);

		Field f2 = SyncRegistrationServiceImpl.class.getDeclaredField("auditHandler");
		f2.setAccessible(true);
		f2.set(syncRegistrationService, auditHandler);

	}

	/**
	 * Gets the sync registration status test.
	 *
	 * @return the sync registration status test
	 */
	@Test
	public void getSyncRegistrationStatusSuccessTest() {

		Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		List<SyncRegistrationDto> syncRegistrationDtos = syncRegistrationService.sync(entities);
		assertEquals("Verifing List returned", syncRegistrationDtos.get(0).getRegistrationId(),
				syncRegistrationDto.getRegistrationId());

		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		Mockito.when(syncRegistrationDao.update(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		List<SyncRegistrationDto> syncRegistrationUpdatedDtos = syncRegistrationService.sync(entities);
		assertEquals("Verifing if list is returned. Expected value should be 1002",
				syncRegistrationDto.getRegistrationId(), syncRegistrationUpdatedDtos.get(0).getRegistrationId());

	}

	/**
	 * Gets the sync registration status failure test.
	 *
	 * @return the sync registration status failure test
	 * @throws TablenotAccessibleException
	 *             the tablenot accessible exception
	 */
	@Test(expected = TablenotAccessibleException.class)
	public void getSyncRegistrationStatusFailureTest() throws TablenotAccessibleException {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenThrow(exp);
		syncRegistrationService.sync(entities);

	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void isPresentSuccessTest() {
		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		boolean result = syncRegistrationService.isPresent("1001");
		assertEquals("Verifing if Registration Id is present in DB. Expected value is true", true, result);
	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void isPresentFailureTest() {
		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(null);
		boolean result = syncRegistrationService.isPresent("15");
		assertEquals("Verifing if Registration Id is present in DB. Expected value is False", false, result);
	}

}
