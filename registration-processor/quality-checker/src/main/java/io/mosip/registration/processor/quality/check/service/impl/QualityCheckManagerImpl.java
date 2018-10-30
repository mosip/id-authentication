package io.mosip.registration.processor.quality.check.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.quality.check.code.QualityCheckerStatusCode;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.exception.QcUserNotFoundException;
import io.mosip.registration.processor.quality.check.exception.RegistrationIdNotFoundException;
import io.mosip.registration.processor.quality.check.exception.ResultNotFoundException;
import io.mosip.registration.processor.status.code.AuditLogTempConstant;

@Component
public class QualityCheckManagerImpl implements QualityCheckManager<String, ApplicantInfoDto, QCUserDto> {
	@Autowired
	private ApplicantInfoDao applicantInfoDao;

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	@Override
	public void assignQCUser(String applicantRegistrationId) {

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
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
		}
		return applicantInfoDtoList;

	}

	@Override
	public List<QCUserDto> updateQCUserStatus(List<QCUserDto> qcUserDtos) {
		Map<QcuserRegistrationIdEntity, QCUserDto> rhmap = new LinkedHashMap<>();
		Map<QcuserRegistrationIdEntity, QCUserDto> hmap = validateUser(qcUserDtos,rhmap);
		List<QCUserDto> resultDtos = new ArrayList<>();
		//QcuserRegistrationIdEntity entity = new QcuserRegistrationIdEntity();
		hmap.forEach((k,v)->{
			k.setStatus(v.getDecisionStatus().name());
			QcuserRegistrationIdEntity entity = applicantInfoDao.update(k);
			resultDtos.add(convertEntityToDto(entity));

		});
//		entities.forEach(entity -> {
//			//qcUserDtos.stream().filter(dto -> )
//			
//			//entity.setStatus(dto.getDecisionStatus().toString());
//			entity = applicantInfoDao.update(entity);
//			resultDtos.add(convertEntityToDto(entity));
//		});
		return resultDtos;
	}

	private Map<QcuserRegistrationIdEntity, QCUserDto> validateUser(List<QCUserDto> qcUserDtos,Map<QcuserRegistrationIdEntity, QCUserDto> hmap) {
		List<QcuserRegistrationIdEntity> entities = new ArrayList<>();
		
		qcUserDtos.forEach(dto -> {

			if (dto.getQcUserId() == null || dto.getQcUserId().isEmpty()) {
				throw new QcUserNotFoundException(
						QualityCheckerStatusCode.QC_USER_ID_NOT_FOUND.name() + " FOR QC USER ID " + dto.getQcUserId());
			}
			if (dto.getRegId() == null || dto.getRegId().isEmpty()) {
				throw new RegistrationIdNotFoundException(QualityCheckerStatusCode.REGISTRATION_ID_NOT_FOUND.name());
			}
			
			QcuserRegistrationIdEntity entity = applicantInfoDao.findById(dto.getQcUserId(), dto.getRegId());
			if (entity == null) {
				throw new ResultNotFoundException(QualityCheckerStatusCode.DATA_NOT_FOUND.name() + " FOR RID :"
						+ dto.getRegId() + " AND  FOR QC USER ID:" + dto.getQcUserId());
			}
			hmap.put(entity,dto);
		//	entities.add(entity);
		});
		return hmap;
	}

	public QCUserDto convertEntityToDto(QcuserRegistrationIdEntity entity) {
		QCUserDto dto = new QCUserDto();
		dto.setQcUserId(entity.getId().getUsrId());
		dto.setRegId(entity.getId().getRegId());
		dto.setDecisionStatus(DecisionStatus.valueOf(entity.getStatus()));
		return dto;
	}

	private void createAuditRequestBuilder(String applicationId, String applicationName, String description,
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
