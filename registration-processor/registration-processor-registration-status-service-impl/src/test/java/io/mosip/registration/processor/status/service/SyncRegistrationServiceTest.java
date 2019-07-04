package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.kernel.idvalidator.rid.constant.RidExceptionProperty;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.decryptor.Decryptor;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncResponseFailureDto;
import io.mosip.registration.processor.status.dto.SyncResponseSuccessDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.impl.SyncRegistrationServiceImpl;

/**
 * The Class SyncRegistrationServiceTest.
 * 
 * @author Ranjitha Siddegowda
 */
@RefreshScope
// @RunWith(SpringRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonUtils.class)
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
	private SyncResponseSuccessDto syncResponseDto;

	/** The ridValidator. */
	@Mock
	private RidValidator<String> ridValidator;

	/** The sync registration service. */
	@InjectMocks
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService = new SyncRegistrationServiceImpl();

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private Decryptor decryptor;

	RegistrationSyncRequestDTO registrationSyncRequestDTO;

	@Mock
	Environment env;
	
	@Mock
	LogDescription description;

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
		registrationSyncRequestDTO = new RegistrationSyncRequestDTO();
		entities = new ArrayList<>();
		Mockito.doNothing().when(description).setMessage(any());
		
		syncRegistrationDto = new SyncRegistrationDto();

		syncRegistrationDto.setRegistrationId("27847657360002520181208183052");
		syncRegistrationDto.setLangCode("ENGLISH");
		syncRegistrationDto.setIsActive(true);
		syncRegistrationDto.setIsDeleted(false);
		syncRegistrationDto.setPacketHashValue("ab123");
		syncRegistrationDto.setSupervisorStatus("APPROVED");
		syncRegistrationDto.setSyncType(SyncTypeDto.NEW.getValue());

		syncRegistrationDto1 = new SyncRegistrationDto();

		syncRegistrationDto1.setRegistrationId("27847657360002520181208183052");
		syncRegistrationDto1.setLangCode("eng");

		syncRegistrationDto1.setIsActive(true);
		syncRegistrationDto1.setIsDeleted(false);

		syncRegistrationDto1.setSyncType("NEW_REGISTRATION");
		syncRegistrationDto1.setPacketHashValue("ab123");
		syncRegistrationDto1.setSupervisorStatus("APPROVED");

		syncRegistrationDto2 = new SyncRegistrationDto();

		syncRegistrationDto2.setRegistrationId("27847657360002520181208183052");
		syncRegistrationDto2.setLangCode("eng");
		syncRegistrationDto2.setIsActive(true);
		syncRegistrationDto2.setIsDeleted(false);

		syncRegistrationDto2.setSyncType(SyncTypeDto.UPDATE.getValue());
		syncRegistrationDto2.setPacketHashValue("ab123");
		syncRegistrationDto2.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto3 = new SyncRegistrationDto();
		syncRegistrationDto3.setRegistrationId("53718436135988");
		syncRegistrationDto3.setLangCode("eng");
		syncRegistrationDto3.setIsActive(true);
		syncRegistrationDto3.setIsDeleted(false);

		syncRegistrationDto3.setSyncType(SyncTypeDto.NEW.getValue());
		syncRegistrationDto3.setPacketHashValue("ab123");
		syncRegistrationDto3.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto4 = new SyncRegistrationDto();
		syncRegistrationDto4.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto4.setLangCode("eng");
		syncRegistrationDto4.setIsActive(true);
		syncRegistrationDto4.setIsDeleted(false);

		syncRegistrationDto4.setSyncType(SyncTypeDto.NEW.getValue());
		syncRegistrationDto4.setPacketHashValue("ab123");
		syncRegistrationDto4.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto5 = new SyncRegistrationDto();
		syncRegistrationDto5.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto5.setLangCode("eng");
		syncRegistrationDto5.setIsActive(true);
		syncRegistrationDto5.setIsDeleted(false);

		syncRegistrationDto5.setSyncType(SyncTypeDto.NEW.getValue());
		syncRegistrationDto5.setPacketHashValue("ab123");
		syncRegistrationDto5.setSupervisorStatus("APPROVED");


		SyncRegistrationDto syncRegistrationDto7 = new SyncRegistrationDto();
		syncRegistrationDto7.setRegistrationId(null);
		syncRegistrationDto7.setLangCode("eng");
		syncRegistrationDto7.setIsActive(true);
		syncRegistrationDto7.setIsDeleted(false);

		syncRegistrationDto7.setSyncType(SyncTypeDto.ACTIVATED.getValue());
		syncRegistrationDto7.setPacketHashValue("ab123");
		syncRegistrationDto7.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto8 = new SyncRegistrationDto();
		syncRegistrationDto8.setRegistrationId("12345678901234567890123456789");
		syncRegistrationDto8.setLangCode("eng");
		syncRegistrationDto8.setIsActive(true);
		syncRegistrationDto8.setIsDeleted(false);

		syncRegistrationDto8.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto8.setPacketHashValue("ab123");
		syncRegistrationDto8.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto9 = new SyncRegistrationDto();
		syncRegistrationDto9.setRegistrationId("27847657360002520181208183050");
		syncRegistrationDto9.setLangCode("eng");
		syncRegistrationDto9.setIsActive(true);
		syncRegistrationDto9.setIsDeleted(false);

		syncRegistrationDto9.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto9.setPacketHashValue("ab123");
		syncRegistrationDto9.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto10 = new SyncRegistrationDto();
		syncRegistrationDto10.setRegistrationId("27847657360002520181208183050");
		syncRegistrationDto10.setLangCode("eng");
		syncRegistrationDto10.setIsActive(true);
		syncRegistrationDto10.setIsDeleted(false);

		syncRegistrationDto10.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto10.setPacketHashValue("ab123");
		syncRegistrationDto10.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto11 = new SyncRegistrationDto();
		syncRegistrationDto11.setRegistrationId("27847657360002520181208183050");
		syncRegistrationDto11.setLangCode("eng");
		syncRegistrationDto11.setIsActive(true);
		syncRegistrationDto11.setIsDeleted(false);

		syncRegistrationDto11.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto11.setPacketHashValue("ab123");
		syncRegistrationDto11.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto12 = new SyncRegistrationDto();
		syncRegistrationDto12.setRegistrationId("12345678901234567890123456799");
		syncRegistrationDto12.setLangCode("eng");
		syncRegistrationDto12.setIsActive(true);
		syncRegistrationDto12.setIsDeleted(false);

		syncRegistrationDto12.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto12.setPacketHashValue("ab123");
		syncRegistrationDto12.setSupervisorStatus("APPROVED");

		SyncRegistrationDto syncRegistrationDto13 = new SyncRegistrationDto();
		syncRegistrationDto13.setRegistrationId("1234567890123456789012345ABCD");
		syncRegistrationDto13.setLangCode("eng");
		syncRegistrationDto13.setIsActive(true);
		syncRegistrationDto13.setIsDeleted(false);

		syncRegistrationDto13.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto13.setPacketHashValue("ab123");
		syncRegistrationDto13.setSupervisorStatus("test");

		SyncRegistrationDto syncRegistrationDto14 = new SyncRegistrationDto();
		syncRegistrationDto14.setRegistrationId("27847657360002520181208183123");
		syncRegistrationDto14.setLangCode("eng");
		syncRegistrationDto14.setIsActive(true);
		syncRegistrationDto14.setIsDeleted(false);

		syncRegistrationDto14.setSyncType(SyncTypeDto.DEACTIVATED.getValue());

		SyncRegistrationDto syncRegistrationDto15 = new SyncRegistrationDto();
		syncRegistrationDto15.setRegistrationId("27847657360002520181208183124");
		syncRegistrationDto15.setLangCode("eng");
		syncRegistrationDto15.setIsActive(true);
		syncRegistrationDto15.setIsDeleted(false);

		syncRegistrationDto15.setSyncType(SyncTypeDto.DEACTIVATED.getValue());
		syncRegistrationDto15.setSupervisorStatus("test");

		entities.add(syncRegistrationDto);
		entities.add(syncRegistrationDto1);
		entities.add(syncRegistrationDto2);
		entities.add(syncRegistrationDto3);
		entities.add(syncRegistrationDto4);
		entities.add(syncRegistrationDto5);
		
		entities.add(syncRegistrationDto7);
		entities.add(syncRegistrationDto8);
		entities.add(syncRegistrationDto9);
		entities.add(syncRegistrationDto10);
		entities.add(syncRegistrationDto11);
		entities.add(syncRegistrationDto12);
		entities.add(syncRegistrationDto13);
		entities.add(syncRegistrationDto14);
		entities.add(syncRegistrationDto15);

		syncResponseDto = new SyncResponseSuccessDto();
		syncResponseDto.setRegistrationId(syncRegistrationDto.getRegistrationId());

		syncResponseDto.setStatus("Success");

		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setId("0c326dc2-ac54-4c2a-98b4-b0c620f1661f");
		syncRegistrationEntity.setRegistrationId(syncRegistrationDto.getRegistrationId());
		syncRegistrationEntity.setRegistrationType(syncRegistrationDto.getRegistrationType().toString());

		syncRegistrationEntity.setLangCode(syncRegistrationDto.getLangCode());

		syncRegistrationEntity.setIsDeleted(syncRegistrationDto.getIsDeleted());
		syncRegistrationEntity.setCreateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setUpdateDateTime(LocalDateTime.now());
		syncRegistrationEntity.setCreatedBy("MOSIP");
		syncRegistrationEntity.setUpdatedBy("MOSIP");

		Mockito.when(ridValidator.validateId(any())).thenReturn(true);

	}

	/**
	 * Gets the sync registration status test.
	 *
	 * @return the sync registration status test
	 */
	@Test
	public void testGetSyncRegistrationStatusSuccess() {

		Mockito.when(syncRegistrationDao.save(any())).thenReturn(syncRegistrationEntity);
		List<SyncResponseDto> syncResponse = syncRegistrationService.sync(entities);

		assertEquals("Verifing List returned", ((SyncResponseFailureDto) syncResponse.get(0)).getRegistrationId(),
				syncRegistrationDto.getRegistrationId());

		Mockito.when(syncRegistrationDao.findById(any())).thenReturn(syncRegistrationEntity);
		Mockito.when(syncRegistrationDao.update(any())).thenReturn(syncRegistrationEntity);
		List<SyncResponseDto> syncResponseDto = syncRegistrationService.sync(entities);
		assertEquals("Verifing if list is returned. Expected value should be 1002",
				syncRegistrationDto.getRegistrationId(),
				((SyncResponseFailureDto) syncResponse.get(0)).getRegistrationId());

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
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(syncRegistrationDao.save(any())).thenThrow(exp);
		syncRegistrationService.sync(entities);

	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void testIsPresentSuccess() {
		Mockito.when(syncRegistrationDao.findById(any())).thenReturn(syncRegistrationEntity);
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
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(),
				RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage());
		Mockito.when(ridValidator.validateId(any())).thenThrow(exp);
		syncRegistrationService.sync(entities);
	}

	/**
	 * Gets the sync rid in valid length failure test.
	 *
	 * @return the sync rid in valid length failure test
	 */
	@Test
	public void getSyncRidInValidLengthFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(),
				RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage());
		Mockito.when(ridValidator.validateId("123456789012345678")).thenThrow(exp);
		syncRegistrationService.sync(entities);
	}

	/**
	 * Gets the sync rid in valid time stamp failure test.
	 *
	 * @return the sync rid in valid time stamp failure test
	 */
	@Test
	public void getSyncRidInValidTimeStampFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorCode(),
				RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorMessage());
		Mockito.when(ridValidator.validateId("12345678901234567890123456799")).thenThrow(exp);
		syncRegistrationService.sync(entities);
	}

	/**
	 * Gets the sync prid in valid length failure test.
	 *
	 * @return the sync prid in valid length failure test
	 */
	@Test
	public void getSyncPridInValidLengthFailureTest() {
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(),
				RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage());
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
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorCode(),
				RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorMessage());
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
		InvalidIDException exp = new InvalidIDException(RidExceptionProperty.INVALID_RID.getErrorCode(),
				RidExceptionProperty.INVALID_RID.getErrorMessage());
		Mockito.when(ridValidator.validateId("1234567890123456789012345ABCD")).thenThrow(exp);
		syncRegistrationService.sync(entities);
	}

	@Test
	public void testdecryptAndGetSyncRequest() throws PacketDecryptionFailureException, ApisResourceAccessException,
			JsonParseException, JsonMappingException, IOException {
		List<SyncResponseDto> syncResponseList = new ArrayList<>();
		Mockito.when(decryptor.decrypt(any(), any(), any())).thenReturn("test");
		PowerMockito.mockStatic(JsonUtils.class);
		PowerMockito.when(JsonUtils.jsonStringToJavaObject(any(), any())).thenReturn(registrationSyncRequestDTO);
		RegistrationSyncRequestDTO regSyncDto = syncRegistrationService.decryptAndGetSyncRequest("", "", "1234",
				syncResponseList);
		assertEquals("decrypted and return the dto", regSyncDto, registrationSyncRequestDTO);

	}

	@Test
	public void testDecryptionException() throws PacketDecryptionFailureException, ApisResourceAccessException,
			JsonParseException, JsonMappingException, IOException {
		List<SyncResponseDto> syncResponseList = new ArrayList<>();
		Mockito.when(decryptor.decrypt(any(), any(), any())).thenThrow(new PacketDecryptionFailureException("", ""));
		PowerMockito.mockStatic(JsonUtils.class);
		PowerMockito.when(JsonUtils.jsonStringToJavaObject(any(), any())).thenReturn(registrationSyncRequestDTO);
		RegistrationSyncRequestDTO regSyncDto = syncRegistrationService.decryptAndGetSyncRequest("", "", "1234",
				syncResponseList);
		assertEquals(1, syncResponseList.size());

	}

	@Test
	public void testJsonParseException() throws PacketDecryptionFailureException, ApisResourceAccessException,
			JsonParseException, JsonMappingException, IOException {
		List<SyncResponseDto> syncResponseList = new ArrayList<>();
		Mockito.when(decryptor.decrypt(any(), any(), any())).thenReturn("test");
		PowerMockito.mockStatic(JsonUtils.class);
		PowerMockito.when(JsonUtils.jsonStringToJavaObject(any(), any()))
				.thenThrow(new JsonParseException("", "", new Throwable()));
		RegistrationSyncRequestDTO regSyncDto = syncRegistrationService.decryptAndGetSyncRequest("", "", "1234",
				syncResponseList);
		assertEquals(1, syncResponseList.size());

	}

	@Test
	public void testJsonMappingException() throws PacketDecryptionFailureException, ApisResourceAccessException,
			JsonParseException, JsonMappingException, IOException {
		List<SyncResponseDto> syncResponseList = new ArrayList<>();
		Mockito.when(decryptor.decrypt(any(), any(), any())).thenReturn("test");
		PowerMockito.mockStatic(JsonUtils.class);
		PowerMockito.when(JsonUtils.jsonStringToJavaObject(any(), any()))
				.thenThrow(new JsonMappingException("", "", new Throwable()));
		RegistrationSyncRequestDTO regSyncDto = syncRegistrationService.decryptAndGetSyncRequest("", "", "1234",
				syncResponseList);
		assertEquals(1, syncResponseList.size());

	}

}