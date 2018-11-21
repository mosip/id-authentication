package io.mosip.registration.processor.quality.check.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.constant.AuditLogConstant;
import io.mosip.registration.processor.core.constant.EventId;
import io.mosip.registration.processor.core.constant.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.auditmanager.requestbuilder.ClientAuditRequestBuilder;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.client.QCUsersClient;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.exception.InvalidQcUserIdException;
import io.mosip.registration.processor.quality.check.exception.InvalidRegistrationIdException;
import io.mosip.registration.processor.quality.check.exception.ResultNotFoundException;
import io.mosip.registration.processor.quality.check.exception.TablenotAccessibleException;


@Component
public class QualityCheckManagerImpl implements QualityCheckManager<String, QCUserDto> {
	@Autowired
	private ApplicantInfoDao applicantInfoDao;

	@Autowired
	QCUsersClient qcUsersClient;

	@Autowired
	ClientAuditRequestBuilder clientAuditRequestBuilder;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	String description = "";

	@Override
	public QCUserDto assignQCUser(String applicantRegistrationId) {
		List<String> qcUsersList = Arrays.asList("qc001","qc002","qc003");
		//qcUsersClient.getAllQcuserIds();

		String qcUserId = qcUsersList.get(new Random().nextInt(qcUsersList.size()));
		QCUserDto qcUserDto = new QCUserDto();
		qcUserDto.setQcUserId(qcUserId);
		qcUserDto.setRegId(applicantRegistrationId);
		qcUserDto.setDecisionStatus(DecisionStatus.PENDING);
		qcUserDto = assignNewPacket(qcUserDto);
		return qcUserDto;
	}


	@Override
	public List<QCUserDto> updateQCUserStatus(List<QCUserDto> qcUserDtos) {
		boolean isTransactionSuccessful = false;
		try {
			Map<QcuserRegistrationIdEntity, QCUserDto> map = validateUser(qcUserDtos);
			List<QCUserDto> resultDtos = new ArrayList<>();

			map.forEach((k, v) -> {
				k.setStatus_code(v.getDecisionStatus().name());
				QcuserRegistrationIdEntity entity = applicantInfoDao.update(k);
				resultDtos.add(convertEntityToDto(entity));

			});
			isTransactionSuccessful = true;
			return resultDtos;

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_QCR_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {
			description = isTransactionSuccessful ? "description--QC User status update successful"
					: "description--QC User status update failed";
			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			clientAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}

	}

	private Map<QcuserRegistrationIdEntity, QCUserDto> validateUser(List<QCUserDto> qcUserDtos) {

		Map<QcuserRegistrationIdEntity, QCUserDto> map = new LinkedHashMap<>();
		qcUserDtos.forEach(dto -> {

			if (dto.getQcUserId() == null || dto.getQcUserId().trim().isEmpty()) {
				throw new InvalidQcUserIdException(PlatformErrorMessages.RPR_QCR_INVALID_QC_USER_ID.getMessage());
			}
			if (dto.getRegId() == null || dto.getRegId().trim().isEmpty()) {
				throw new InvalidRegistrationIdException(PlatformErrorMessages.RPR_QCR_INVALID_REGISTRATION_ID.getMessage());
			}

			QcuserRegistrationIdEntity entity = applicantInfoDao.findById(dto.getQcUserId(), dto.getRegId());
			if (entity == null) {
				throw new ResultNotFoundException(PlatformErrorMessages.RPR_QCR_RESULT_NOT_FOUND.getMessage() + " FOR RID: "
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
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_QCR_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {
			description = isTransactionSuccessful ? "description--Demographic-data saved Success"
					: "description--Demographic Failed to save";
			eventId = isTransactionSuccessful ? EventId.RPR_401.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventName.GET.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_401.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			clientAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}
	}

	private QcuserRegistrationIdEntity convertDtoToEntity(QCUserDto qcUserDto) {
		QcuserRegistrationIdEntity qcUserEntity = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity qcuserPKEntity = new QcuserRegistrationIdPKEntity();
		qcuserPKEntity.setRegId(qcUserDto.getRegId());
		qcuserPKEntity.setUsrId(qcUserDto.getQcUserId());

		qcUserEntity.setId(qcuserPKEntity);
		qcUserEntity.setStatus_code(qcUserDto.getDecisionStatus().name());

		return qcUserEntity;

	}

	public QCUserDto convertEntityToDto(QcuserRegistrationIdEntity entity) {
		QCUserDto dto = new QCUserDto();
		dto.setQcUserId(entity.getId().getUsrId());
		dto.setRegId(entity.getId().getRegId());
		dto.setDecisionStatus(DecisionStatus.valueOf(entity.getStatus_code()));
		return dto;
	}

}
