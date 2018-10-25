/**
 * 
 */
package io.mosip.registration.processor.status.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.hibernate.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.code.AuditLogTempConstant;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.utilities.RegistrationUtility;

/**
 * The Class SyncRegistrationServiceImpl.
 *
 * @author M1048399
 * @author M1048219
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncRegistrationDto> {

	/** The Constant TABLE_NOT_ACCESSIBLE. */
	private static final String TABLE_NOT_ACCESSIBLE = "Could not fetch data from table";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "MOSIP";

	/** The sync registration dao. */
	@Autowired
	private SyncRegistrationDao syncRegistrationDao;

	/** The audit request builder. */
	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	/** The audit handler. */
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	/**
	 * Instantiates a new sync registration service impl.
	 */
	public SyncRegistrationServiceImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.status.service.SyncRegistrationService#sync(
	 * java.util.List)
	 */
	@Override
	public List<SyncRegistrationDto> sync(List<SyncRegistrationDto> resgistrationDtos) {

		List<SyncRegistrationDto> list = new ArrayList<>();

		boolean isTransactionSuccessful = false;
		try {
			for (SyncRegistrationDto registrationDto : resgistrationDtos) {
				SyncRegistrationEntity existingSyncRegistration = findByRegistrationId(
						registrationDto.getRegistrationId().trim());
				SyncRegistrationEntity syncRegistration;
				if (existingSyncRegistration != null) {
					// update sync registration record
					syncRegistration = convertDtoToEntity(registrationDto);
					syncRegistration.setId(existingSyncRegistration.getId());
					syncRegistration.setCreateDateTime(existingSyncRegistration.getCreateDateTime());
					syncRegistration = syncRegistrationDao.update(syncRegistration);
				} else {
					// first time sync registration
					syncRegistration = convertDtoToEntity(registrationDto);
					syncRegistration.setId(RegistrationUtility.generateId());
					syncRegistration = syncRegistrationDao.save(syncRegistration);
				}
				list.add(convertEntityToDto(syncRegistration));
			}
			isTransactionSuccessful = true;
			return list;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(TABLE_NOT_ACCESSIBLE, e);
		} finally {

			String description = isTransactionSuccessful ? "description--sync Success" : "description--sync Failure";
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.status.service.SyncRegistrationService#
	 * isPresent(java.lang.String)
	 */
	@Override
	public boolean isPresent(String registrationId) {
		return findByRegistrationId(registrationId) != null;
	}

	/**
	 * Find by registration id.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the sync registration entity
	 */
	private SyncRegistrationEntity findByRegistrationId(String registrationId) {
		return syncRegistrationDao.findById(registrationId);
	}

	/**
	 * Convert entity to dto.
	 *
	 * @param entity
	 *            the entity
	 * @return the sync registration dto
	 */
	private SyncRegistrationDto convertEntityToDto(SyncRegistrationEntity entity) {
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setRegistrationId(entity.getRegistrationId());
		syncRegistrationDto.setIsActive(entity.getIsActive());
		syncRegistrationDto.setIsDeleted(entity.getIsDeleted());
		syncRegistrationDto.setLangCode(entity.getLangCode());
		syncRegistrationDto.setParentRegistrationId(entity.getParentRegistrationId());
		syncRegistrationDto.setStatusComment(entity.getStatusComment());
		syncRegistrationDto.setSyncStatus(SyncStatusDto.valueOf(entity.getStatusCode()));
		syncRegistrationDto.setSyncType(SyncTypeDto.valueOf(entity.getRegistrationType()));

		return syncRegistrationDto;
	}

	/**
	 * Convert dto to entity.
	 *
	 * @param dto
	 *            the dto
	 * @return the sync registration entity
	 */
	private SyncRegistrationEntity convertDtoToEntity(SyncRegistrationDto dto) {
		SyncRegistrationEntity syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId(dto.getRegistrationId().trim());
		syncRegistrationEntity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE);
		syncRegistrationEntity.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : Boolean.FALSE);
		syncRegistrationEntity.setLangCode(dto.getLangCode());
		syncRegistrationEntity.setParentRegistrationId(dto.getParentRegistrationId());
		syncRegistrationEntity.setStatusComment(dto.getStatusComment());
		syncRegistrationEntity.setStatusCode(dto.getSyncStatus().toString());
		syncRegistrationEntity.setRegistrationType(dto.getSyncType().toString());
		syncRegistrationEntity.setCreatedBy(CREATED_BY);
		syncRegistrationEntity.setUpdatedBy(CREATED_BY);
		if (syncRegistrationEntity.getIsDeleted()) {
			syncRegistrationEntity.setDeletedDateTime(LocalDateTime.now());
		} else {
			syncRegistrationEntity.setDeletedDateTime(null);
		}

		return syncRegistrationEntity;
	}

	/**
	 * Creates the audit request builder.
	 *
	 * @param applicationId
	 *            the application id
	 * @param applicationName
	 *            the application name
	 * @param description
	 *            the description
	 * @param eventId
	 *            the event id
	 * @param eventName
	 *            the event name
	 * @param eventType
	 *            the event type
	 */
	public void createAuditRequestBuilder(String applicationId, String applicationName, String description,
			String eventId, String eventName, String eventType) {
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId(applicationId)
				.setApplicationName(applicationName).setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
				.setDescription(description).setEventId(eventId).setEventName(eventName).setEventType(eventType)
				.setHostIp(AuditLogTempConstant.HOST_IP.toString())
				.setHostName(AuditLogTempConstant.HOST_NAME.toString()).setId(AuditLogTempConstant.ID.toString())
				.setIdType(AuditLogTempConstant.ID_TYPE.toString())
				.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
				.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
				.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
				.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());

		AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}
}
