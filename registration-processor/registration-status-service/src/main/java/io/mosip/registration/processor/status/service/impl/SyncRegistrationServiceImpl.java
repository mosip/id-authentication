/**
 * 
 */
package io.mosip.registration.processor.status.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
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
 * @author M1047487
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncRegistrationDto> {

	/** The Constant TABLE_NOT_ACCESSIBLE. */
	private static final String TABLE_NOT_ACCESSIBLE = "Could not fetch data from table";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "MOSIP";

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The sync registration dao. */
	@Autowired
	private SyncRegistrationDao syncRegistrationDao;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

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
					eventId = EventId.RPR_402.toString();
				} else {
					// first time sync registration
					syncRegistration = convertDtoToEntity(registrationDto);
					syncRegistration.setId(RegistrationUtility.generateId());
					syncRegistration = syncRegistrationDao.save(syncRegistration);
					eventId = EventId.RPR_407.toString();
				}
				list.add(convertEntityToDto(syncRegistration));
			}
			isTransactionSuccessful = true;
			return list;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			String  description = "";
			if (isTransactionSuccessful) {
				eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
						: EventName.ADD.toString();
				eventType = EventType.BUSINESS.toString();
				description = "Registartion Id's are successfully synched in Sync Registration table";
			} else {
				eventId = EventId.RPR_405.toString();
				eventName = EventName.EXCEPTION.toString();
				eventType = EventType.SYSTEM.toString();
				description = "Registartion Id's sync is unsuccessful";
			}
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,AuditLogConstant.MULTIPLE_ID.toString());

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
}
