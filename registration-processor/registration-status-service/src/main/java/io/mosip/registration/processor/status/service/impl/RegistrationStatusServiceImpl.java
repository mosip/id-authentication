package io.mosip.registration.processor.status.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.status.code.TransactionTypeCode;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;

/**
 * The Class RegistrationStatusServiceImpl.
 */
@Component
public class RegistrationStatusServiceImpl implements RegistrationStatusService<String, RegistrationStatusDto> {

	/** The threshold time. */
	@Value("${landingZone_To_VirusScan_Interval_Threshhold_time}")

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
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.status.service.RegistrationStatusService#getRegistrationStatus(java.lang.Object)
	 */
	@Override
	public RegistrationStatusDto getRegistrationStatus(String registrationId) {
		boolean isTransactionSuccessful = false;
		try {
			RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
			isTransactionSuccessful = true;
			//Event constants for audit log
			eventId = EventId.RPR_401.toString();
			eventName = EventName.GET.toString();
			eventType = EventType.BUSINESS.toString();
			return entity != null ? convertEntityToDto(entity) : null;
		} catch (DataAccessLayerException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {

			description = isTransactionSuccessful ? "Get registration status by registration id is successfull"
					: "Get registration status by registration id is unsuccessfull";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.status.service.RegistrationStatusService#findbyfilesByThreshold(java.lang.String)
	 */
	@Override
	public List<RegistrationStatusDto> findbyfilesByThreshold(String statusCode) {
		boolean isTransactionSuccessful = false;
		try {
			List<RegistrationStatusEntity> entities = registrationStatusDao.findbyfilesByThreshold(statusCode,
					getThreshholdTime());
			isTransactionSuccessful = true;
			//Event constants for audit log
			eventId = EventId.RPR_401.toString();
			eventName = EventName.GET.toString();
			eventType = EventType.BUSINESS.toString();
			return convertEntityListToDtoList(entities);
		} catch (DataAccessLayerException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {

			description = isTransactionSuccessful ? "Find files by threshold time and statuscode is successfull"
					: "Find files by threshold time and statuscode is unsuccessfull";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.status.service.RegistrationStatusService#addRegistrationStatus(java.lang.Object)
	 */
	@Override
	public void addRegistrationStatus(RegistrationStatusDto registrationStatusDto) {
		boolean isTransactionSuccessful = false;
		String transactionId = generateId();
		registrationStatusDto.setLatestRegistrationTransactionId(transactionId);
		try {
			RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
			registrationStatusDao.save(entity);
			isTransactionSuccessful = true;
			//Event constants for audit log
			eventId = EventId.RPR_407.toString();
			eventName = EventName.SAVE.toString();
			eventType = EventType.BUSINESS.toString();
			TransactionDto transactionDto = new TransactionDto(transactionId, registrationStatusDto.getRegistrationId(),
					null, TransactionTypeCode.CREATE.toString(), "Added registration status record",
					registrationStatusDto.getStatusCode(), registrationStatusDto.getStatusComment());
			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType("Added registration record");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		} catch (DataAccessLayerException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			throw new TablenotAccessibleException("Could not add Information to table", e);
		} finally {

			description = isTransactionSuccessful ? "Registration status added successfully"
					: "Registration status unsuccessfull";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationStatusDto.getRegistrationId());
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.status.service.RegistrationStatusService#updateRegistrationStatus(java.lang.Object)
	 */
	@Override
	public void updateRegistrationStatus(RegistrationStatusDto registrationStatusDto) {
		boolean isTransactionSuccessful = false;
		String latestTransactionId = getLatestTransactionId(registrationStatusDto.getRegistrationId());
		registrationStatusDto.setLatestRegistrationTransactionId(latestTransactionId);
		try {
			RegistrationStatusDto dto = getRegistrationStatus(registrationStatusDto.getRegistrationId());
			if (dto != null) {
				RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
				registrationStatusDao.save(entity);
				isTransactionSuccessful = true;
				//Event constants for audit log
				eventId = EventId.RPR_407.toString();
				eventName = EventName.SAVE.toString();
				eventType = EventType.BUSINESS.toString();
				TransactionDto transactionDto = new TransactionDto(generateId(),
						registrationStatusDto.getRegistrationId(), latestTransactionId,
						TransactionTypeCode.UPDATE.toString(), "updated registration status record",
						registrationStatusDto.getStatusCode(), registrationStatusDto.getStatusComment());
				transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
				transactionDto.setReferenceIdType("updated registration record");
				transcationStatusService.addRegistrationTransaction(transactionDto);
			}
		} catch (DataAccessLayerException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			throw new TablenotAccessibleException("Could not update Information to table", e);
		} finally {

			description = isTransactionSuccessful ? "Updated registration status successfully"
					: "Updated registration status unsuccessfully";			
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationStatusDto.getRegistrationId());

		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.status.service.RegistrationStatusService#getByStatus(java.lang.String)
	 */
	@Override
	public List<RegistrationStatusDto> getByStatus(String status) {
		boolean isTransactionSuccessful = false;
		try {
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getEnrolmentStatusByStatusCode(status);
			isTransactionSuccessful = true;
			//Event constants for audit log
			eventId = EventId.RPR_401.toString();
			eventName = EventName.GET.toString();
			eventType = EventType.BUSINESS.toString();
			return convertEntityListToDtoList(registrationStatusEntityList);
		} catch (DataAccessLayerException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {

			description = isTransactionSuccessful ? "Get list of registration status by status successfully"
					: "Get list of registration status by status unsuccessfully";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.MULTIPLE_ID.toString());

		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.status.service.RegistrationStatusService#getByIds(java.lang.String)
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
			//Event constants for audit log
			eventId = EventId.RPR_401.toString();
			eventName = EventName.GET.toString();
			eventType = EventType.BUSINESS.toString();
			return convertEntityListToDtoList(registrationStatusEntityList);

		} catch (DataAccessLayerException e) {
			//Event constants for audit log
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {

			description = isTransactionSuccessful ? "Get list of registration status by registration id successfully"
					: "Get list of registration status by registration id unsuccessfully";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.MULTIPLE_ID.toString());
		}
	}

	/**
	 * Convert entity list to dto list.
	 *
	 * @param entities the entities
	 * @return the list
	 */
	private List<RegistrationStatusDto> convertEntityListToDtoList(List<RegistrationStatusEntity> entities) {
		List<RegistrationStatusDto> list = new ArrayList<>();
		if (entities != null) {
			for (RegistrationStatusEntity entity : entities) {
				list.add(convertEntityToDto(entity));
			}

		}
		return list;
	}

	/**
	 * Convert entity to dto.
	 *
	 * @param entity the entity
	 * @return the registration status dto
	 */
	private RegistrationStatusDto convertEntityToDto(RegistrationStatusEntity entity) {
		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();
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

	/**
	 * Convert dto to entity.
	 *
	 * @param dto the dto
	 * @return the registration status entity
	 */
	private RegistrationStatusEntity convertDtoToEntity(RegistrationStatusDto dto) {
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
	 * @param registrationId the registration id
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
