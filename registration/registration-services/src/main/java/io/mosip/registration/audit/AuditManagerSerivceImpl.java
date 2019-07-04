package io.mosip.registration.audit;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegPacketStatusService;

/**
 * Class to Audit the events of Registration Client.
 * <p>
 * This class creates a wrapper around {@link AuditRequestBuilder} class. This
 * class creates a {@link AuditRequestBuilder} object for each audit event and
 * persists the same using {@link AuditHandler} .
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AuditManagerSerivceImpl extends BaseService implements AuditManagerService {

	private static final Logger LOGGER = AppConfig.getLogger(AuditManagerSerivceImpl.class);
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;
	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private RegPacketStatusService regPacketStatusService;

	@Autowired
	private AuditLogControlDAO auditLogControlDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.audit.AuditFactory#audit(io.mosip.registration.
	 * constants.AuditEvent, io.mosip.registration.constants.Components,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId, String refIdType) {

		// Getting Host IP Address and Name
		String hostIP = String.valueOf(ApplicationContext.map().get(RegistrationConstants.DEFAULT_HOST_IP));
		String hostName = String.valueOf(ApplicationContext.map().get(RegistrationConstants.DEFAULT_HOST_NAME));
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException unknownHostException) {
			LOGGER.info("REGISTRATION-AUDIT_FACTORY-AUDIT", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(unknownHostException));
		}

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		auditRequestBuilder.setActionTimeStamp(LocalDateTime.now(ZoneOffset.UTC))
				.setApplicationId(String.valueOf(ApplicationContext.map().get(RegistrationConstants.APP_ID)))
				.setApplicationName(String.valueOf(ApplicationContext.map().get(RegistrationConstants.APP_NAME)))
				.setCreatedBy(SessionContext.userName()).setDescription(auditEventEnum.getDescription())
				.setEventId(auditEventEnum.getId()).setEventName(auditEventEnum.getName())
				.setEventType(auditEventEnum.getType()).setHostIp(hostIP).setHostName(hostName).setId(refId)
				.setIdType(refIdType).setModuleId(appModuleEnum.getId()).setModuleName(appModuleEnum.getName())
				.setSessionUserId(SessionContext.userId()).setSessionUserName(SessionContext.userName());
		auditHandler.addAudit(auditRequestBuilder.build());
	}

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
				cal.setTime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
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
