/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.batchjobservices.entity.RegistrationBookingEntity;
import io.mosip.preregistration.batchjobservices.exception.util.BatchServiceExceptionCatcher;
import io.mosip.preregistration.batchjobservices.repository.dao.BatchServiceDAO;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ExpiredStatusService {

	/** The Constant LOGGER. */
	private Logger log = LoggerConfiguration.logConfig(ExpiredStatusService.class);

	
	
	@Value("${version}")
	String versionUrl;

	@Value("${mosip.preregistration.batchjob.service.expired.id}")
	String idUrl;

	@Autowired
	private BatchServiceDAO batchServiceDAO;
	
	@Autowired
	AuditLogUtil auditLogUtil;
	
	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}


	/**
	 * @return Response dto
	 */
	
	public MainResponseDTO<String> expireAppointments() {

		LocalDate currentDate = LocalDate.now();
		MainResponseDTO<String> response = new MainResponseDTO<>();
		response.setId(idUrl);
		response.setVersion(versionUrl);
		boolean isSaveSuccess=false;
		List<RegistrationBookingEntity> bookedPreIdList = null;
		try {
			bookedPreIdList = batchServiceDAO.getAllOldDateBooking(currentDate);

			bookedPreIdList.forEach(iterate -> {
				String preRegId = iterate.getBookingPK().getPreregistrationId();
				DemographicEntity demographicEntity = batchServiceDAO.getApplicantDemographicDetails(preRegId);
				if (demographicEntity != null) {

					if (demographicEntity.getStatusCode().equals(StatusCodes.BOOKED.getCode())) {
						demographicEntity.setStatusCode(StatusCodes.EXPIRED.getCode());
						batchServiceDAO.updateApplicantDemographic(demographicEntity);
					}
					log.info("sessionId", "idType", "id", "Update the status successfully into Registration Appointment table and Demographic table for Pre-RegistrationId: "+preRegId);
				}
			});
			isSaveSuccess=true;

		} catch (Exception e) {
			new BatchServiceExceptionCatcher().handle(e,response);
		}
		finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_413.toString(), EventName.EXPIREDSTATUS.toString(),
						EventType.BUSINESS.toString(),
						"Updated the expired status & the expired PreRegistration ids successfully saved in the database",
						AuditLogVariables.PRE_REGISTRATION_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Expired status failed to update", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), null);
			}
		}
		response.setResponsetime(GenericUtil.getCurrentResponseTime());
		response.setResponse("Registration appointment status updated to expired successfully");
		return response;
	}
	
	/**
	 * This method is used to audit all the consumed status events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName, String refId) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setDescription(description);
		auditRequestDto.setIdType(idType);
		auditRequestDto.setId(refId);
		auditRequestDto.setModuleId(AuditLogVariables.BAT.toString());
		auditRequestDto.setModuleName(AuditLogVariables.EXPIRED_BATCH_SERVICE.toString());
		
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}



}
