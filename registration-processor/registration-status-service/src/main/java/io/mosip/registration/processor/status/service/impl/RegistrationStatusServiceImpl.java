package io.mosip.registration.processor.status.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.TransactionTypeCode;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;
import io.mosip.registration.processor.status.utilities.RegistrationStatusMapUtil;

/**
 * The Class RegistrationStatusServiceImpl.
 */
@Component
public class RegistrationStatusServiceImpl
		implements RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> {

	@Value("${registration.processor.landingZone_To_VirusScan_Interval_Threshhold_time}")

	private int threshholdTime;

	/** The registration status dao. */
	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	/** The transcation status service. */
	@Autowired
	private TransactionService<TransactionDto> transcationStatusService;

	/** The Constant COULD_NOT_GET. */
	private static final String COULD_NOT_GET = "Could not get Information from table";

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	String description = "";

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.status.service.RegistrationStatusService#
	 * getRegistrationStatus(java.lang.Object)
	 */
	@Override
	public InternalRegistrationStatusDto getRegistrationStatus(String registrationId) {
		boolean isTransactionSuccessful = false;
		try {
			RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
			isTransactionSuccessful = true;
			return entity != null ? convertEntityToDto(entity) : null;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "Get registration status by registration id is successful"
					: "Get registration status by registration id is unsuccessful";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.status.service.RegistrationStatusService#
	 * findbyfilesByThreshold(java.lang.String)
	 */
	@Override
	public List<InternalRegistrationStatusDto> findbyfilesByThreshold(String statusCode) {
		boolean isTransactionSuccessful = false;
		try {
			List<RegistrationStatusEntity> entities = registrationStatusDao.findbyfilesByThreshold(statusCode,
					getThreshholdTime());
			isTransactionSuccessful = true;
			return convertEntityListToDtoList(entities);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "Find files by threshold time and statuscode is successful"
					: "Find files by threshold time and statuscode is unsuccessful";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.status.service.RegistrationStatusService#
	 * addRegistrationStatus(java.lang.Object)
	 */
	@Override
	public void addRegistrationStatus(InternalRegistrationStatusDto registrationStatusDto) {
		boolean isTransactionSuccessful = false;
		String transactionId = generateId();
		registrationStatusDto.setLatestRegistrationTransactionId(transactionId);
		try {
			RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
			registrationStatusDao.save(entity);
			isTransactionSuccessful = true;
			TransactionDto transactionDto = new TransactionDto(transactionId, registrationStatusDto.getRegistrationId(),
					null, TransactionTypeCode.CREATE.toString(), "Added registration status record",
					registrationStatusDto.getStatusCode(), registrationStatusDto.getStatusComment());
			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType("Added registration record");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "Registration status added successfully"
					: "Failure in adding registration status to registration table";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationStatusDto.getRegistrationId());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.status.service.RegistrationStatusService#
	 * updateRegistrationStatus(java.lang.Object)
	 */
	@Override
	public void updateRegistrationStatus(InternalRegistrationStatusDto registrationStatusDto) {
		boolean isTransactionSuccessful = false;
		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());
		registrationStatusDto.setLatestRegistrationTransactionId(latestTransactionId);
		try {
			InternalRegistrationStatusDto dto = getRegistrationStatus(registrationStatusDto.getRegistrationId());
			if (dto != null) {
				RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
				registrationStatusDao.save(entity);
				isTransactionSuccessful = true;
				TransactionDto transactionDto = new TransactionDto(generateId(),
						registrationStatusDto.getRegistrationId(), latestTransactionId,
						TransactionTypeCode.UPDATE.toString(), "updated registration status record",
						registrationStatusDto.getStatusCode(), registrationStatusDto.getStatusComment());
				transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
				transactionDto.setReferenceIdType("updated registration record");
				transcationStatusService.addRegistrationTransaction(transactionDto);
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "Updated registration status successfully"
					: "Updated registration status unsuccessfully";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationStatusDto.getRegistrationId());

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.status.service.RegistrationStatusService#
	 * getByStatus(java.lang.String)
	 */
	@Override
	public List<InternalRegistrationStatusDto> getByStatus(String status) {
		boolean isTransactionSuccessful = false;
		try {
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getEnrolmentStatusByStatusCode(status);
			isTransactionSuccessful = true;
			return convertEntityListToDtoList(registrationStatusEntityList);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "Get list of registration status by status successfully"
					: "Get list of registration status by status unsuccessfully";
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.MULTIPLE_ID.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.status.service.RegistrationStatusService#
	 * getByIds(java.lang.String)
	 */
	@Override
	public List<RegistrationStatusDto> getByIds(String ids) {
		boolean isTransactionSuccessful = false;
		try {
			String[] registrationIdArray = ids.split(",");
			List<String> registrationIds = Arrays.asList(registrationIdArray);
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getByIds(registrationIds);
			isTransactionSuccessful = true;
			return convertEntityListToDtoListAndGetExternalStatus(registrationStatusEntityList);

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful
					? "Get list of registration status by registration id successfully"
					: "Get list of registration status by registration id unsuccessfully";
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.MULTIPLE_ID.toString());
		}
	}

	private List<RegistrationStatusDto> convertEntityListToDtoListAndGetExternalStatus(
			List<RegistrationStatusEntity> entities) {
		List<RegistrationStatusDto> list = new ArrayList<>();
		if (entities != null) {
			for (RegistrationStatusEntity entity : entities) {
				list.add(convertEntityToDtoAndGetExternalStatus(entity));
			}

		}
		return list;
	}

	private RegistrationStatusDto convertEntityToDtoAndGetExternalStatus(RegistrationStatusEntity entity) {
		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();
		registrationStatusDto.setRegistrationId(entity.getId());
		Map<RegistrationStatusCode, RegistrationExternalStatusCode> statusMap = RegistrationStatusMapUtil
				.statusMapper();
		// get the mapped value for the entity StatusCode
		String mappedValue = statusMap.get(RegistrationStatusCode.valueOf(entity.getStatusCode())).toString();
		registrationStatusDto.setStatusCode(mappedValue);
		return registrationStatusDto;
	}

	private List<InternalRegistrationStatusDto> convertEntityListToDtoList(List<RegistrationStatusEntity> entities) {
		List<InternalRegistrationStatusDto> list = new ArrayList<>();
		if (entities != null) {
			for (RegistrationStatusEntity entity : entities) {
				list.add(convertEntityToDto(entity));
			}

		}
		return list;
	}

	private InternalRegistrationStatusDto convertEntityToDto(RegistrationStatusEntity entity) {
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRegistrationId(entity.getId());
		registrationStatusDto.setRegistrationType(entity.getRegistrationType());
		registrationStatusDto.setReferenceRegistrationId(entity.getReferenceRegistrationId());
		registrationStatusDto.setStatusCode(entity.getStatusCode());
		registrationStatusDto.setLangCode(entity.getLangCode());
		registrationStatusDto.setStatusComment(entity.getStatusComment());
		registrationStatusDto.setLatestRegistrationTransactionId(entity.getLatestRegistrationTransactionId());
		registrationStatusDto.setIsActive(entity.isActive());
		registrationStatusDto.setCreatedBy(entity.getCreatedBy());
		registrationStatusDto.setCreateDateTime(entity.getCreateDateTime());
		registrationStatusDto.setUpdatedBy(entity.getUpdatedBy());
		registrationStatusDto.setUpdateDateTime(entity.getUpdateDateTime());
		registrationStatusDto.setIsDeleted(entity.isDeleted());
		registrationStatusDto.setDeletedDateTime(entity.getDeletedDateTime());
		registrationStatusDto.setRetryCount(entity.getRetryCount());
		return registrationStatusDto;
	}

	private RegistrationStatusEntity convertDtoToEntity(InternalRegistrationStatusDto dto) {
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity();
		registrationStatusEntity.setId(dto.getRegistrationId());
		registrationStatusEntity.setRegistrationType(dto.getRegistrationType());
		registrationStatusEntity.setReferenceRegistrationId(dto.getReferenceRegistrationId());
		registrationStatusEntity.setStatusCode(dto.getStatusCode());
		registrationStatusEntity.setLangCode(dto.getLangCode());
		registrationStatusEntity.setStatusComment(dto.getStatusComment());
		registrationStatusEntity.setLatestRegistrationTransactionId(dto.getLatestRegistrationTransactionId());
		registrationStatusEntity.setIsActive(dto.isActive());
		registrationStatusEntity.setCreatedBy(dto.getCreatedBy());
		registrationStatusEntity.setCreateDateTime(dto.getCreateDateTime());
		registrationStatusEntity.setUpdatedBy(dto.getUpdatedBy());
		registrationStatusEntity.setUpdateDateTime(dto.getUpdateDateTime());
		registrationStatusEntity.setIsDeleted(dto.isDeleted());
		registrationStatusEntity.setDeletedDateTime(dto.getDeletedDateTime());
		registrationStatusEntity.setRetryCount(dto.getRetryCount());
		return registrationStatusEntity;
	}

	/**
	 * Gets the latest transaction id.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the latest transaction id
	 */
	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	/**
	 * Gets the threshhold time.
	 *
	 * @return the threshhold time
	 */
	public int getThreshholdTime() {
		return this.threshholdTime;
	}

	/**
	 * Generate id.
	 *
	 * @return the string
	 */
	public String generateId() {
		return UUID.randomUUID().toString();
	}

}
