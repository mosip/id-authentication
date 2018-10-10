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
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
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
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncRegistrationDto> {

	private static final String TABLE_NOT_ACCESSIBLE = "Could not fetch data from table";
	private static final String CREATED_BY = "MOSIP";

	@Autowired
	private SyncRegistrationDao syncRegistrationDao;

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	/**
	 * Instantiates a new sync registration service impl.
	 */
	public SyncRegistrationServiceImpl() {
		super();
	}

	@Override
	public List<SyncRegistrationDto> sync(List<SyncRegistrationDto> resgistrationDtos) {

		List<SyncRegistrationDto> list = new ArrayList<>();

		boolean isTransactionSuccessful = false;
		try {
			for (SyncRegistrationDto registrationDto : resgistrationDtos) {
				SyncRegistrationEntity existingSyncRegistration = findByRegistrationId(
						registrationDto.getRegistrationId());
				SyncRegistrationEntity syncRegistration;
				if (existingSyncRegistration != null) {
					// update sync registration record
					syncRegistration = convertDtoToEntity(registrationDto);
					syncRegistration.setSyncRegistrationId(existingSyncRegistration.getSyncRegistrationId());
					syncRegistration.setCreateDateTime(existingSyncRegistration.getCreateDateTime());
					syncRegistration = syncRegistrationDao.update(syncRegistration);
				} else {
					// first time sync registration
					syncRegistration = convertDtoToEntity(registrationDto);
					syncRegistration.setSyncRegistrationId(RegistrationUtility.generateId());
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

	@Override
	public boolean isPresent(String registrationId) {
		return findByRegistrationId(registrationId) != null;
	}

	private SyncRegistrationEntity findByRegistrationId(String registrationId) {
		return syncRegistrationDao.findById(registrationId);
	}

	private SyncRegistrationDto convertEntityToDto(SyncRegistrationEntity entity) {
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setRegistrationId(entity.getRegistrationId());
		syncRegistrationDto.setIsActive(entity.getIsActive());
		syncRegistrationDto.setIsDeleted(entity.getIsDeleted());
		syncRegistrationDto.setLangCode(entity.getLangCode());
		syncRegistrationDto.setParentRegistrationId(entity.getParentRegistrationId());
		syncRegistrationDto.setStatusComment(entity.getStatusComment());
		syncRegistrationDto.setSyncStatusDto(SyncStatusDto.valueOf(entity.getStatusCode()));
		syncRegistrationDto.setSyncTypeDto(SyncTypeDto.valueOf(entity.getRegistrationType()));

		return syncRegistrationDto;
	}

	private SyncRegistrationEntity convertDtoToEntity(SyncRegistrationDto dto) {
		SyncRegistrationEntity syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId(dto.getRegistrationId());
		syncRegistrationEntity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE);
		syncRegistrationEntity.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : Boolean.FALSE);
		syncRegistrationEntity.setLangCode(dto.getLangCode());
		syncRegistrationEntity.setParentRegistrationId(dto.getParentRegistrationId());
		syncRegistrationEntity.setStatusComment(dto.getStatusComment());
		syncRegistrationEntity.setStatusCode(dto.getSyncStatusDto().toString());
		syncRegistrationEntity.setRegistrationType(dto.getSyncTypeDto().toString());
		syncRegistrationEntity.setCreatedBy(CREATED_BY);
		syncRegistrationEntity.setUpdatedBy(CREATED_BY);
		if (syncRegistrationEntity.getIsDeleted()) {
			syncRegistrationEntity.setDeletedDateTime(LocalDateTime.now());
		} else {
			syncRegistrationEntity.setDeletedDateTime(null);
		}

		return syncRegistrationEntity;
	}

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
