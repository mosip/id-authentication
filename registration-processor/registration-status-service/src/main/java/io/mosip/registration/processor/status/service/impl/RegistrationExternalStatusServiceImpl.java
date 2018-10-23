package io.mosip.registration.processor.status.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.code.AuditLogTempConstant;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dao.RegistrationExternalStatusDao;
import io.mosip.registration.processor.status.dto.RegistrationExternalStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationExternalStatusService;
import io.mosip.registration.processor.status.utilities.RegistrationStatusMapUtil;

@Component
public class RegistrationExternalStatusServiceImpl
		implements RegistrationExternalStatusService<RegistrationExternalStatusDto> {

	@Autowired
	private RegistrationExternalStatusDao registrationExternalStatusDao;

	/** The audit request builder. */
	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	/** The audit handler. */
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	private static final String TABLE_NOT_ACCESSIBLE = "Could not fetch data from table";

	@Override
	public List<RegistrationExternalStatusDto> getStatus(String ids) {
		boolean isTransactionSuccessful = false;
		try {
			String[] registrationIdArray = ids.split(",");
			List<String> registrationIds = Arrays.asList(registrationIdArray);
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationExternalStatusDao
					.getByIds(registrationIds);
			isTransactionSuccessful = true;

			return convertEntityListToDtoList(registrationStatusEntityList);

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(TABLE_NOT_ACCESSIBLE, e);
		} finally {
			String description = isTransactionSuccessful ? "description--getStatus Success"
					: "description--getStatus Failure";
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
		}

	}

	private List<RegistrationExternalStatusDto> convertEntityListToDtoList(List<RegistrationStatusEntity> entities) {
		List<RegistrationExternalStatusDto> list = new ArrayList<>();
		if (entities != null) {
			entities.forEach(entity -> list.add(convertEntityToDto(entity)));

		}

		return list;
	}

	private RegistrationExternalStatusDto convertEntityToDto(RegistrationStatusEntity entity) {
		RegistrationExternalStatusDto registrationExternalStatusDto = new RegistrationExternalStatusDto();
		Map<RegistrationStatusCode, RegistrationExternalStatusCode> statusMap = RegistrationStatusMapUtil
				.statusMapper();
		// get the mapped value for the entity StatusCode
		String mappedValue = statusMap.get(RegistrationStatusCode.valueOf(entity.getStatusCode())).toString();
		registrationExternalStatusDto.setStatusCode(mappedValue);

		return registrationExternalStatusDto;
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
