/**
 * 
 */
package io.mosip.registration.processor.status.service.impl;

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
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class SyncRegistrationServiceImpl.
 *
 * @author M1048399
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncRegistrationDto> {

	@Autowired
	private SyncRegistrationDao syncRegistrationDao;


	private static final String COULD_NOT_GET = "Could not get Information from table";
	
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
	public List<SyncRegistrationDto> sync(List<SyncRegistrationDto> syncResgistrationdto) {
		List<SyncRegistrationDto> list = new ArrayList<>();
		boolean isTransactionSuccessful = false;
		try {
			for (SyncRegistrationDto syncRegistrationDto : syncResgistrationdto) {
				if (!isPresent(syncRegistrationDto.getRegistrationId())) {
					list.add(convertEntityToDto(syncRegistrationDao.save(convertDtoToEntity(syncRegistrationDto))));
				}
			}
			isTransactionSuccessful = true;
			return list;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			String description = "";
			if (isTransactionSuccessful) {
				description = "description--sync Success";
			} else {
				description = "description--sync Failure";
			}
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
		}

	}

	@Override
	public boolean isPresent(String syncResgistrationId) {
		return syncRegistrationDao.findById(syncResgistrationId)!=null;
	}
	

	
	/**
	 * Convert entity to dto.
	 *
	 * @param entities
	 *            the entities
	 * @return the list
	 */
	@SuppressWarnings("unused")
	private List<SyncRegistrationDto> convertEntityToDto(List<SyncRegistrationEntity> entities) {
		List<SyncRegistrationDto> ents = new ArrayList<>();
		for(SyncRegistrationEntity entity: entities) {
			ents.add(convertEntityToDto(entity));
		}
		return ents;
	}

	/**
	 * Convert dto to entity.
	 *
	 * @param entities
	 *            the entities
	 * @return the list
	 */
	private List<SyncRegistrationEntity> convertDtoToEntity(List<SyncRegistrationDto> entities) {
		List<SyncRegistrationEntity> ents = new ArrayList<>();
		for(SyncRegistrationDto entity: entities) {
			ents.add(convertDtoToEntity(entity));
		}
		return ents;
	}

	private SyncRegistrationDto convertEntityToDto(SyncRegistrationEntity entity) {
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setRegistrationId(entity.getRegistrationId());
		syncRegistrationDto.setIsActive(entity.getIsActive());
		syncRegistrationDto.setIsDeleted(entity.getIsDeleted());
		syncRegistrationDto.setLangCode(entity.getLangCode());
		syncRegistrationDto.setParentRegistrationId(entity.getParentRegistrationId());
		syncRegistrationDto.setStatusCode(entity.getStatusCode());
		syncRegistrationDto.setSyncRegistrationId(entity.getSyncRegistrationId());
		syncRegistrationDto.setStatusComment(entity.getStatusComment());
		
		syncRegistrationDto.setCreatedBy(entity.getCreatedBy());
		syncRegistrationDto.setCreateDateTime(entity.getCreateDateTime());
		syncRegistrationDto.setUpdatedBy(entity.getUpdatedBy());
		syncRegistrationDto.setUpdateDateTime(entity.getUpdateDateTime());
		syncRegistrationDto.setDeletedDateTime(entity.getDeletedDateTime());
		return syncRegistrationDto;
	}

	private SyncRegistrationEntity convertDtoToEntity(SyncRegistrationDto dto) {
		SyncRegistrationEntity syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId(dto.getRegistrationId());
		syncRegistrationEntity.setIsActive(dto.getIsActive());
		syncRegistrationEntity.setIsDeleted(dto.getIsDeleted());
		syncRegistrationEntity.setLangCode(dto.getLangCode());
		syncRegistrationEntity.setParentRegistrationId(dto.getParentRegistrationId());
		syncRegistrationEntity.setStatusCode(dto.getStatusCode());
		syncRegistrationEntity.setSyncRegistrationId(dto.getSyncRegistrationId());
		syncRegistrationEntity.setStatusComment(dto.getStatusComment());
		
		syncRegistrationEntity.setCreatedBy(dto.getCreatedBy());
		syncRegistrationEntity.setCreateDateTime(dto.getCreateDateTime());
		syncRegistrationEntity.setUpdatedBy(dto.getUpdatedBy());
		syncRegistrationEntity.setUpdateDateTime(dto.getUpdateDateTime());
		syncRegistrationEntity.setDeletedDateTime(dto.getDeletedDateTime());
		return syncRegistrationEntity;
	}
	
	public void createAuditRequestBuilder(String applicationId,String applicationName,String description,String eventId,String eventName,String eventType){
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
		.setApplicationId(applicationId)
		.setApplicationName(applicationName)
		.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
		.setDescription(description)
		.setEventId(eventId)
		.setEventName(eventName)
		.setEventType(eventType)
		.setHostIp(AuditLogTempConstant.HOST_IP.toString())
		.setHostName(AuditLogTempConstant.HOST_NAME.toString())
		.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
		.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
		.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
		.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
		.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
		
        AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}
}
