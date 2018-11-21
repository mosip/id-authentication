package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
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

	/** The sync registration service. */
	@InjectMocks
	private SyncRegistrationService<SyncRegistrationDto> syncRegistrationService = new SyncRegistrationServiceImpl();

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

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
		syncRegistrationDto.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto.setSyncType(SyncTypeDto.NEW_REGISTRATION);
		entities.add(syncRegistrationDto);

		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setId("0c326dc2-ac54-4c2a-98b4-b0c620f1661f");
		syncRegistrationEntity.setRegistrationId(syncRegistrationDto.getRegistrationId());
		syncRegistrationEntity.setRegistrationType(syncRegistrationDto.getSyncType().toString());
		syncRegistrationEntity.setParentRegistrationId(syncRegistrationDto.getParentRegistrationId());
		syncRegistrationEntity.setStatusCode(syncRegistrationDto.getSyncStatus().toString());
		syncRegistrationEntity.setStatusComment(syncRegistrationDto.getStatusComment());
		syncRegistrationEntity.setLangCode(syncRegistrationDto.getLangCode());
		syncRegistrationEntity.setIsActive(syncRegistrationDto.getIsActive());
		syncRegistrationEntity.setIsDeleted(syncRegistrationDto.getIsDeleted());
		syncRegistrationEntity.setCreateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setUpdateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setCreatedBy("MOSIP");
		syncRegistrationEntity.setUpdatedBy("MOSIP");

		AuditResponseDto auditResponseDto=new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder("test case description",EventId.RPR_401.toString(),EventName.ADD.toString(),EventType.BUSINESS.toString(), "1234testcase");


		/*AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		AuditRequestDto auditRequest1 = new AuditRequestDto();

		Field f = CoreAuditRequestBuilder.class.getDeclaredField("auditRequestBuilder");
		f.setAccessible(true);
		f.set(clientAuditRequestBuilder, auditRequestBuilder);

		Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
		f1.setAccessible(true);
		f1.set(auditRequestBuilder, auditRequest1);

		Field f2 = CoreAuditRequestBuilder.class.getDeclaredField("auditHandler");
		f2.setAccessible(true);
		f2.set(coreAuditRequestBuilder, auditHandler);*/

	}

	/**
	 * Gets the sync registration status test.
	 *
	 * @return the sync registration status test
	 */
	@Test
	public void testGetSyncRegistrationStatusSuccess() {

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
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(), "errorMessage",
				new Exception());
		Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenThrow(exp);
		syncRegistrationService.sync(entities);

	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void testIsPresentSuccess() {
		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		boolean result = syncRegistrationService.isPresent("1001");
		assertEquals("Verifing if Registration Id is present in DB. Expected value is true", true, result);
	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void testIsPresentFailure() {
		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(null);
		boolean result = syncRegistrationService.isPresent("15");
		assertEquals("Verifing if Registration Id is present in DB. Expected value is False", false, result);
	}

}
