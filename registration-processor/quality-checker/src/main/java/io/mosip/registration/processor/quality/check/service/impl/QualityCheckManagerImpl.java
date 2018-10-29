package io.mosip.registration.processor.quality.check.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
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
		boolean isTransactionSuccessful= false;
		List<ApplicantInfoDto>  applicantInfoDtoList=null;
		try {
			applicantInfoDtoList = applicantInfoDao.getPacketsforQCUser(qcuserId);
			isTransactionSuccessful=true;
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
	public void updateQCUserStatus(List<QCUserDto> qcUserDtos) {
		qcUserDtos.forEach(dto -> {
			if (applicantInfoDao.findById(dto.getQcUserId()) != null) {
				QcuserRegistrationIdEntity qcUserEntity = convertDtoToEntity(dto);
				applicantInfoDao.update(qcUserEntity);
			}
		});
	}

	private QcuserRegistrationIdEntity convertDtoToEntity(QCUserDto qcUserDto) {
		QcuserRegistrationIdEntity qcUserEntity = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity qcuserPKEntity = new QcuserRegistrationIdPKEntity();
		qcuserPKEntity.setRegId(qcUserDto.getRegId());
		qcuserPKEntity.setUsrId(qcUserDto.getQcUserId());

		qcUserEntity.setId(qcuserPKEntity);
		qcUserEntity.setStatus(qcUserDto.getDecisionStatus());

		return qcUserEntity;

	}

	// private QCUserDto convertEntityToDto(QcuserRegistrationIdEntity qcUserEntity)
	// {
	// QCUserDto qcUserDto = new QCUserDto();
	//
	//
	// }

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
