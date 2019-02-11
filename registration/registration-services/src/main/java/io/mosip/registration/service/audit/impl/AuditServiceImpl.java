/**
 * 
 */
package io.mosip.registration.service.audit.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.audit.AuditService;
import io.mosip.registration.service.packet.RegPacketStatusService;

/**
 * Audit Service
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Service
public class AuditServiceImpl extends BaseService implements AuditService {

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private RegPacketStatusService regPacketStatusService;

	@Autowired
	private AuditLogControlDAO auditLogControlDAO;

	

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuditServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.audit.AuditService#deleteAuditLogs()
	 */
	@Override
	public synchronized ResponseDTO deleteAuditLogs() {

		LOGGER.info(LoggerConstants.AUDIT_SERVICE_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Deletion of Audit Logs Started");

		ResponseDTO responseDTO = new ResponseDTO();

		String val = getGlobalConfigValueOf(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS);

		if (val != null) {
			try {
				int auditDeletionConfiguredDays = Integer.parseInt(val);

				/* Get Calendar instance */
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Timestamp(System.currentTimeMillis()));
				cal.add(Calendar.DATE, -auditDeletionConfiguredDays);

				/* To-Date */
				Timestamp req = new Timestamp(cal.getTimeInMillis());

				/* Fetch Audit Log Controls Using req Time */
				List<AuditLogControl> auditLogControls = auditLogControlDAO.get(req);

				if (isNull(auditLogControls) || isEmpty(auditLogControls)) {

					/* No Audit Logs Found */
					return setSuccessResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_EMPTY_MSG, null);

				}

				List<String> regIds = auditLogControls.stream().map(auditControl -> {
					return auditControl.getRegistrationId();
				}).collect(Collectors.toList());

				/* Fetch Registartions to be deleted */
				List<Registration> registrations = registrationDAO.get(regIds);

				/* Delete Registrations */
				regPacketStatusService.deleteRegistrations(registrations);

				setSuccessResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, null);

			} catch (RuntimeException runtimeException) {
				LOGGER.error(LoggerConstants.AUDIT_SERVICE_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

				setErrorResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, null);

			}

		} else {
			setErrorResponse(responseDTO, RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, null);
		}

		LOGGER.info(LoggerConstants.AUDIT_SERVICE_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Deletion of Audit Logs Completed");

		return responseDTO;
	}

	
}
