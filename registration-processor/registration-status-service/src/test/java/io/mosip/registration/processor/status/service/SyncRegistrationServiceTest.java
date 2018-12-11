package io.mosip.registration.processor.status.service;

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

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.kernel.idvalidator.rid.constant.RidExceptionProperty;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
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
	
	/** The sync registration dto. */
	private SyncRegistrationDto syncRegistrationDto1;
	
	/** The sync registration dto. */
	private SyncRegistrationDto syncRegistrationDto2;

	/** The sync registration entity. */
	private SyncRegistrationEntity syncRegistrationEntity;

	/** The entities. */
	private List<SyncRegistrationDto> entities;

	/** The sync registration dao. */
	@Mock
	private SyncRegistrationDao syncRegistrationDao;
	
	/** The sync response dto. */
	@Mock
	private SyncResponseDto syncResponseDto;
	
	/** The ridValidator. */
	@Mock
	private RidValidator<String> ridValidator;

	/** The sync registration service. */
	@InjectMocks
	private SyncRegistrationService<SyncResponseDto,SyncRegistrationDto> syncRegistrationService = new SyncRegistrationServiceImpl();

	/** The audit log request builder. */
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

		syncRegistrationDto.setRegistrationId("27847657360002520181208183052");
		syncRegistrationDto.setLangCode("ENGLISH");
		syncRegistrationDto.setIsActive(true);
		syncRegistrationDto.setIsDeleted(false);
		syncRegistrationDto.setParentRegistrationId("53718436135988");
		syncRegistrationDto.setStatusComment("NEW");
		syncRegistrationDto.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto.setSyncType(SyncTypeDto.NEW.getValue());
		
		syncRegistrationDto1 = new SyncRegistrationDto();
		
		syncRegistrationDto1.setRegistrationId("27847657360002520181208183052");
		syncRegistrationDto1.setLangCode("eng");
		syncRegistrationDto1.setIsActive(true);
		syncRegistrationDto1.setIsDeleted(false);
		syncRegistrationDto1.setParentRegistrationId("53718436135988");
		syncRegistrationDto1.setStatusComment("NEW");
		syncRegistrationDto1.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto1.setSyncType("NEW_REGISTRATION");
		
		syncRegistrationDto2 = new SyncRegistrationDto();
		
		syncRegistrationDto2.setRegistrationId("27847657360002520181208183052");
		syncRegistrationDto2.setLangCode("eng");
		syncRegistrationDto2.setIsActive(true);
		syncRegistrationDto2.setIsDeleted(false);
		syncRegistrationDto2.setParentRegistrationId("53718436135988");
		syncRegistrationDto2.setStatusComment("NEW");
		syncRegistrationDto2.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto2.setSyncType(SyncTypeDto.UPDATE.getValue());
		
		SyncRegistrationDto syncRegistrationDto3 = new SyncRegistrationDto();
		syncRegistrationDto3.setRegistrationId("53718436135988");
		syncRegistrationDto3.setLangCode("eng");
		syncRegistrationDto3.setIsActive(true);
		syncRegistrationDto3.setIsDeleted(false);
		syncRegistrationDto3.setParentRegistrationId("53718436135988");
		syncRegistrationDto3.setStatusComment("NEW");
		syncRegistrationDto3.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto3.setSyncType(SyncTypeDto.CORRECTION.getValue());
		
		SyncRegistrationDto syncRegistrationDto4 = new SyncRegistrationDto();
		syncRegistrationDto4.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto4.setLangCode("eng");
		syncRegistrationDto4.setIsActive(true);
		syncRegistrationDto4.setIsDeleted(false);
		syncRegistrationDto4.setParentRegistrationId("53718436135988");
		syncRegistrationDto4.setStatusComment("NEW");
		syncRegistrationDto4.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto4.setSyncType(SyncTypeDto.LOST_UIN.getValue());
		
		SyncRegistrationDto syncRegistrationDto5 = new SyncRegistrationDto();
		syncRegistrationDto5.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto5.setLangCode("eng");
		syncRegistrationDto5.setIsActive(true);
		syncRegistrationDto5.setIsDeleted(false);
		syncRegistrationDto5.setParentRegistrationId("53718436135988");
		syncRegistrationDto5.setStatusComment("NEW");
		syncRegistrationDto5.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto5.setSyncType(SyncTypeDto.NEW.getValue());
		
		SyncRegistrationDto syncRegistrationDto6 = new SyncRegistrationDto();
		syncRegistrationDto6.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto6.setLangCode("eng");
		syncRegistrationDto6.setIsActive(true);
		syncRegistrationDto6.setIsDeleted(false);
		syncRegistrationDto6.setParentRegistrationId("53718436135988");
		syncRegistrationDto6.setStatusComment("NEW");
		syncRegistrationDto6.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto6.setSyncType(SyncTypeDto.UPDATE_UIN.getValue());
		
		SyncRegistrationDto syncRegistrationDto7 = new SyncRegistrationDto();
		syncRegistrationDto7.setRegistrationId(null);
		syncRegistrationDto7.setLangCode("eng");
		syncRegistrationDto7.setIsActive(true);
		syncRegistrationDto7.setIsDeleted(false);
		syncRegistrationDto7.setParentRegistrationId("53718436135988");
		syncRegistrationDto7.setStatusComment("NEW");
		syncRegistrationDto7.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto7.setSyncType(SyncTypeDto.ACTIVATE_UIN.getValue());
		
		SyncRegistrationDto syncRegistrationDto8 = new SyncRegistrationDto();
		syncRegistrationDto8.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto8.setLangCode("eng");
		syncRegistrationDto8.setIsActive(true);
		syncRegistrationDto8.setIsDeleted(false);
		syncRegistrationDto8.setParentRegistrationId(null);
		syncRegistrationDto8.setStatusComment("NEW");
		syncRegistrationDto8.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto8.setSyncType(SyncTypeDto.DEACTIVATE_UIN.getValue());
		
		SyncRegistrationDto syncRegistrationDto9 = new SyncRegistrationDto();
		syncRegistrationDto9.setRegistrationId("27847657360002520181208183050");
		syncRegistrationDto9.setLangCode("eng");
		syncRegistrationDto9.setIsActive(true);
		syncRegistrationDto9.setIsDeleted(false);
		syncRegistrationDto9.setParentRegistrationId("12345678901234567890123456799");
		syncRegistrationDto9.setStatusComment("NEW");
		syncRegistrationDto9.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto9.setSyncType(SyncTypeDto.DEACTIVATE_UIN.getValue());
		
		SyncRegistrationDto syncRegistrationDto10 = new SyncRegistrationDto();
		syncRegistrationDto10.setRegistrationId("27847657360002520181208183050");
		syncRegistrationDto10.setLangCode("eng");
		syncRegistrationDto10.setIsActive(true);
		syncRegistrationDto10.setIsDeleted(false);
		syncRegistrationDto10.setParentRegistrationId("1234567890123456789012345ABCD");
		syncRegistrationDto10.setStatusComment("NEW");
		syncRegistrationDto10.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto10.setSyncType(SyncTypeDto.DEACTIVATE_UIN.getValue());
		
		SyncRegistrationDto syncRegistrationDto11 = new SyncRegistrationDto();
		syncRegistrationDto11.setRegistrationId("27847657360002520181208183050");
		syncRegistrationDto11.setLangCode("eng");
		syncRegistrationDto11.setIsActive(true);
		syncRegistrationDto11.setIsDeleted(false);
		syncRegistrationDto11.setParentRegistrationId("123456789012345678");
		syncRegistrationDto11.setStatusComment("NEW");
		syncRegistrationDto11.setSyncStatus(SyncStatusDto.PRE_SYNC);
		syncRegistrationDto11.setSyncType(SyncTypeDto.DEACTIVATE_UIN.getValue());
		
		entities.add(syncRegistrationDto);
		entities.add(syncRegistrationDto1);
		entities.add(syncRegistrationDto2);
		entities.add(syncRegistrationDto3);
		entities.add(syncRegistrationDto4);
		entities.add(syncRegistrationDto5);
		entities.add(syncRegistrationDto6);
		entities.add(syncRegistrationDto7);
		entities.add(syncRegistrationDto8);
		entities.add(syncRegistrationDto9);
		entities.add(syncRegistrationDto10);
		entities.add(syncRegistrationDto11);
		
		syncResponseDto = new SyncResponseDto();
		syncResponseDto.setRegistrationId(syncRegistrationDto.getRegistrationId());
		syncResponseDto.setParentRegistrationId(syncRegistrationDto.getParentRegistrationId());
		syncResponseDto.setStatus("Success");	
		syncResponseDto.setMessage("Successfully synched");


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

		Mockito.when(ridValidator.validateId(ArgumentMatchers.any())).thenReturn(true);

	}

	/**
	 * Gets the sync registration status test.
	 *
	 * @return the sync registration status test
	 */
	@Test
	public void testGetSyncRegistrationStatusSuccess() {

		Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		List<SyncResponseDto> syncResponse = syncRegistrationService.sync(entities);
		assertEquals("Verifing List returned", syncResponse.get(0).getRegistrationId(),
				syncRegistrationDto.getRegistrationId());

		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		Mockito.when(syncRegistrationDao.update(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		List<SyncResponseDto> syncResponseDto = syncRegistrationService.sync(entities);
		assertEquals("Verifing if list is returned. Expected value should be 1002",
				syncRegistrationDto.getRegistrationId(), syncResponseDto.get(0).getRegistrationId());

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
	 * Gets the sync registration id failure test.
	 *
	 * @return the sync registration id failure test
	 */
	@Test
	public void getSyncRegistrationIdFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(), RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage());
		Mockito.when(ridValidator.validateId(ArgumentMatchers.any())).thenThrow(exp);
		syncRegistrationService.sync(entities);	
	}
	
	/**
	 * Gets the sync prid in valid length failure test.
	 *
	 * @return the sync prid in valid length failure test
	 */
	@Test
	public void getSyncPridInValidLengthFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(), RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage());
		Mockito.when(ridValidator.validateId("123456789012345678")).thenThrow(exp);
		syncRegistrationService.sync(entities);	
	}
	
	/**
	 * Gets the sync prid in valid time stamp failure test.
	 *
	 * @return the sync prid in valid time stamp failure test
	 */
	@Test
	public void getSyncPridInValidTimeStampFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorCode(), RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorMessage());
		Mockito.when(ridValidator.validateId("12345678901234567890123456799")).thenThrow(exp);
		syncRegistrationService.sync(entities);	
	}
	
	/**
	 * Gets the sync prid in valid format failure test.
	 *
	 * @return the sync prid in valid format failure test
	 */
	@Test
	public void getSyncPridInValidFormatFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID.getErrorCode(), RidExceptionProperty.INVALID_RID.getErrorMessage());
		Mockito.when(ridValidator.validateId("1234567890123456789012345ABCD")).thenThrow(exp);
		syncRegistrationService.sync(entities);	
	}
}