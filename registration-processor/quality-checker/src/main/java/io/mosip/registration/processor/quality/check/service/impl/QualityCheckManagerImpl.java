package io.mosip.registration.processor.quality.check.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.code.QualityCheckerStatusCode;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dao.QCUserInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.exception.InvalidQcUserIdException;
import io.mosip.registration.processor.quality.check.exception.InvalidRegistrationIdException;
import io.mosip.registration.processor.quality.check.exception.ResultNotFoundException;
import io.mosip.registration.processor.quality.check.exception.TablenotAccessibleException;
//import io.mosip.registration.processor.status.code.AuditLogTempConstant;

@Component
public class QualityCheckManagerImpl implements QualityCheckManager<String, ApplicantInfoDto, QCUserDto> {
	@Autowired
	private ApplicantInfoDao applicantInfoDao;

	@Autowired
	private QCUserInfoDao qcUserInfoDao;

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	@Override
	public QCUserDto assignQCUser(String applicantRegistrationId) {
		List<String> qcUsersList = qcUserInfoDao.getAllQcuserIds();
		String qcUserId = qcUsersList.get(new Random().nextInt(qcUsersList.size()));
		QCUserDto qcUserDto = new QCUserDto();
		qcUserDto.setQcUserId(qcUserId);
		qcUserDto.setRegId(applicantRegistrationId);
		qcUserDto.setDecisionStatus(DecisionStatus.PENDING);
		qcUserDto = assignNewPacket(qcUserDto);
		return qcUserDto;
	}

	@Override
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		boolean isTransactionSuccessful = false;
		List<ApplicantInfoDto> applicantInfoDtoList = null;
		try {
			applicantInfoDtoList = applicantInfoDao.getPacketsforQCUser(qcuserId);
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			String description = isTransactionSuccessful ? "description--Demographic-data saved Success"
					: "description--Demographic Failed to save";
			/*
			 * createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
			 * AuditLogTempConstant.APPLICATION_NAME.toString(), description,
			 * AuditLogTempConstant.EVENT_ID.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString());
			 */
		}
		return applicantInfoDtoList;

	}

	@Override
	public List<QCUserDto> updateQCUserStatus(List<QCUserDto> qcUserDtos) {
		boolean isTransactionSuccessful = false;
		try {
			Map<QcuserRegistrationIdEntity, QCUserDto> map = validateUser(qcUserDtos);
			List<QCUserDto> resultDtos = new ArrayList<>();

			map.forEach((k, v) -> {
				k.setStatus(v.getDecisionStatus().name());
				QcuserRegistrationIdEntity entity = applicantInfoDao.update(k);
				resultDtos.add(convertEntityToDto(entity));

			});
			isTransactionSuccessful = true;
			return resultDtos;

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			String description = isTransactionSuccessful ? "description--QC User status update successful"
					: "description--QC User status update failed";
			/*
			 * createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
			 * AuditLogTempConstant.APPLICATION_NAME.toString(), description,
			 * AuditLogTempConstant.EVENT_ID.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString());
			 */
		}

	}

	private Map<QcuserRegistrationIdEntity, QCUserDto> validateUser(List<QCUserDto> qcUserDtos) {

		Map<QcuserRegistrationIdEntity, QCUserDto> map = new LinkedHashMap<>();
		qcUserDtos.forEach(dto -> {

			if (dto.getQcUserId() == null || dto.getQcUserId().trim().isEmpty()) {
				throw new InvalidQcUserIdException(
						QualityCheckerStatusCode.INVALID_QC_USER_ID.name() + ": QC USER ID IS NULL");
			}
			if (dto.getRegId() == null || dto.getRegId().trim().isEmpty()) {
				throw new InvalidRegistrationIdException(
						QualityCheckerStatusCode.INVALID_REGISTRATION_ID.name() + ": REGISTRATION ID IS NULL");
			}

			QcuserRegistrationIdEntity entity = applicantInfoDao.findById(dto.getQcUserId(), dto.getRegId());
			if (entity == null) {
				throw new ResultNotFoundException(QualityCheckerStatusCode.DATA_NOT_FOUND.name() + " FOR RID: "
						+ dto.getRegId() + " AND  FOR QC USER ID: " + dto.getQcUserId());
			}
			map.put(entity, dto);
		});

		return map;
	}

	private QCUserDto assignNewPacket(QCUserDto qcUserDto) {
		boolean isTransactionSuccessful = false;
		try {
			QcuserRegistrationIdEntity qcUserEntity = convertDtoToEntity(qcUserDto);
			applicantInfoDao.save(qcUserEntity);
			isTransactionSuccessful = true;

			return convertEntityToDto(qcUserEntity);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("qcuser_registration_id Table Not Accessible", e);
		} finally {
			String description = isTransactionSuccessful ? "description--Demographic-data saved Success"
					: "description--Demographic Failed to save";
			/*
			 * createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
			 * AuditLogTempConstant.APPLICATION_NAME.toString(), description,
			 * AuditLogTempConstant.EVENT_ID.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString());
			 */
		}
	}

	private QcuserRegistrationIdEntity convertDtoToEntity(QCUserDto qcUserDto) {
		QcuserRegistrationIdEntity qcUserEntity = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity qcuserPKEntity = new QcuserRegistrationIdPKEntity();
		qcuserPKEntity.setRegId(qcUserDto.getRegId());
		qcuserPKEntity.setUsrId(qcUserDto.getQcUserId());

		qcUserEntity.setId(qcuserPKEntity);
		qcUserEntity.setStatus(qcUserDto.getDecisionStatus().name());

		return qcUserEntity;

	}

	public QCUserDto convertEntityToDto(QcuserRegistrationIdEntity entity) {
		QCUserDto dto = new QCUserDto();
		dto.setQcUserId(entity.getId().getUsrId());
		dto.setRegId(entity.getId().getRegId());
		dto.setDecisionStatus(DecisionStatus.valueOf(entity.getStatus()));
		return dto;
	}

	/*
	 * private void createAuditRequestBuilder(String applicationId, String
	 * applicationName, String description, String eventId, String eventName, String
	 * eventType) {
	 * auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId
	 * (applicationId)
	 * .setApplicationName(applicationName).setCreatedBy(AuditLogTempConstant.
	 * CREATED_BY.toString())
	 * .setDescription(description).setEventId(eventId).setEventName(eventName).
	 * setEventType(eventType) .setHostIp(AuditLogTempConstant.HOST_IP.toString())
	 * .setHostName(AuditLogTempConstant.HOST_NAME.toString()).setId(
	 * AuditLogTempConstant.ID.toString())
	 * .setIdType(AuditLogTempConstant.ID_TYPE.toString())
	 * .setModuleId(AuditLogTempConstant.MODULE_ID.toString())
	 * .setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
	 * .setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
	 * .setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());
	 * 
	 * AuditRequestDto auditRequestDto = auditRequestBuilder.build();
	 * auditHandler.writeAudit(auditRequestDto); }
	 */
}
