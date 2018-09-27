package org.mosip.registration.processor.status.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import org.mosip.kernel.auditmanager.request.AuditRequestDto;
import org.mosip.kernel.core.spi.auditmanager.AuditHandler;
import org.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import org.mosip.registration.processor.status.code.AuditLogTempConstant;
import org.mosip.registration.processor.status.code.TransactionTypeCode;
import org.mosip.registration.processor.status.dao.RegistrationStatusDao;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.dto.TransactionDto;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.mosip.registration.processor.status.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStatusServiceImpl implements RegistrationStatusService<String, RegistrationStatusDto> {

	@Value("${landingZone_To_VirusScan_Interval_Threshhold_time}")

	private int threshholdTime;

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Autowired
	private TransactionService<TransactionDto> transcationStatusService;

	private static final String COULD_NOT_GET = "Could not get Information from table";

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	@Override
	public RegistrationStatusDto getRegistrationStatus(String registrationId) {
		boolean isTransactionSuccessful = false;
		try {
			RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
			isTransactionSuccessful = true;
			return entity != null ? convertEntityToDto(entity) : null;
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			if (isTransactionSuccessful) {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--getRegistrationStatus Success")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			} else {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--getRegistrationStatus Failure")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			}

			AuditRequestDto auditRequestDto = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequestDto);

		}
	}

	@Override
	public List<RegistrationStatusDto> findbyfilesByThreshold(String statusCode) {
		boolean isTransactionSuccessful = false;
		try {
			List<RegistrationStatusEntity> entities = registrationStatusDao.findbyfilesByThreshold(statusCode,
					getThreshholdTime());
			isTransactionSuccessful = true;
			return convertEntityListToDtoList(entities);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			if (isTransactionSuccessful) {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--findbyfilesByThreshold Success")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			} else {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--findbyfilesByThreshold Failure")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			}

			AuditRequestDto auditRequestDto = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequestDto);

		}
	}

	@Override
	public void addRegistrationStatus(RegistrationStatusDto registrationStatusDto) {
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
			throw new TablenotAccessibleException("Could not add Information to table", e);
		} finally {
			if (isTransactionSuccessful) {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--addRegistrationStatus Success")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			} else {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--addRegistrationStatus Failure")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			}

			AuditRequestDto auditRequestDto = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequestDto);

		}
	}

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
				TransactionDto transactionDto = new TransactionDto(generateId(),
						registrationStatusDto.getRegistrationId(), latestTransactionId,
						TransactionTypeCode.UPDATE.toString(), "updated registration status record",
						registrationStatusDto.getStatusCode(), registrationStatusDto.getStatusComment());
				transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
				transactionDto.setReferenceIdType("updated registration record");
				transcationStatusService.addRegistrationTransaction(transactionDto);
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Could not update Information to table", e);
		} finally {
			if (isTransactionSuccessful) {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--updateRegistrationStatus Success")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			} else {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--updateRegistrationStatus Failure")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			}

			AuditRequestDto auditRequestDto = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequestDto);

		}
	}

	@Override
	public List<RegistrationStatusDto> getByStatus(String status) {
		boolean isTransactionSuccessful = false;
		try {
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getEnrolmentStatusByStatusCode(status);
			isTransactionSuccessful = true;
			return convertEntityListToDtoList(registrationStatusEntityList);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			if (isTransactionSuccessful) {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--getByStatus Success")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			} else {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--getByStatus Failure")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			}

			AuditRequestDto auditRequestDto = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequestDto);

		}
	}

	@Override
	public List<RegistrationStatusDto> getByIds(String ids) {
		boolean isTransactionSuccessful = false;
		try {
			String[] registrationIdArray = ids.split(",");
			List<String> registrationIds = Arrays.asList(registrationIdArray);
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getByIds(registrationIds);
			isTransactionSuccessful = true;
			return convertEntityListToDtoList(registrationStatusEntityList);

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			if (isTransactionSuccessful) {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--getByIds Success")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			} else {
				auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now())
						.setApplicationId(AuditLogTempConstant.APPLICATION_ID.toString())
						.setApplicationName(AuditLogTempConstant.APPLICATION_NAME.toString())
						.setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
						.setDescription("description--getByIds Failure")
						.setEventId(AuditLogTempConstant.EVENT_ID.toString())
						.setEventName(AuditLogTempConstant.EVENT_NAME.toString())
						.setEventType(AuditLogTempConstant.EVENT_TYPE.toString())
						.setHostIp(AuditLogTempConstant.HOST_IP.toString())
						.setHostName(AuditLogTempConstant.HOST_NAME.toString())
						.setId(AuditLogTempConstant.ID.toString()).setIdType(AuditLogTempConstant.ID_TYPE.toString())
						.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
						.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
						.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
						.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
			}

			AuditRequestDto auditRequestDto = auditRequestBuilder.build();
			auditHandler.writeAudit(auditRequestDto);

		}
	}

	private List<RegistrationStatusDto> convertEntityListToDtoList(List<RegistrationStatusEntity> entities) {
		List<RegistrationStatusDto> list = new ArrayList<>();
		if (entities != null) {
			for (RegistrationStatusEntity entity : entities) {
				list.add(convertEntityToDto(entity));
			}

		}
		return list;
	}

	private RegistrationStatusDto convertEntityToDto(RegistrationStatusEntity entity) {
		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();
		registrationStatusDto.setRegistrationId(entity.getRegistrationId());
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

	private RegistrationStatusEntity convertDtoToEntity(RegistrationStatusDto dto) {
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity();
		registrationStatusEntity.setRegistrationId(dto.getRegistrationId());
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

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	public int getThreshholdTime() {
		return this.threshholdTime;
	}

	public String generateId() {
		return UUID.randomUUID().toString();
	}

}
