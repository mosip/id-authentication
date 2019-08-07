package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegistrationApprovalService;
import io.mosip.registration.util.advice.AuthenticationAdvice;
import io.mosip.registration.util.advice.PreAuthorizeUserId;

/**
 * Implementation class of {@link RegistrationApprovalService} interface
 *
 * @author Mahesh Kumar
 * @since 1.0.0
 */
@Service
public class RegistrationApprovalServiceImpl extends BaseService implements RegistrationApprovalService {

	/**
	 * Object for Registration DAO
	 */
	@Autowired
	private RegistrationDAO registrationDAO;

	/**
	 * Object for Logger
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationApprovalServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditManagerService auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.RegistrationApprovalService#
	 * getEnrollmentByStatus(java.lang.String)
	 */
	@Override
	public List<RegistrationApprovalDTO> getEnrollmentByStatus(String status) throws RegBaseCheckedException {
		LOGGER.info(LoggerConstants.LOG_GET_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Packets list by status started");
		auditFactory.audit(AuditEvent.PACKET_RETRIVE, Components.PACKET_RETRIVE,
				SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		List<RegistrationApprovalDTO> list = new ArrayList<>();
		if (nullCheckForgetEnrollementByStatus(status)) {

			try {

				List<Registration> details = registrationDAO.getEnrollmentByStatus(status);

				LOGGER.info(LoggerConstants.LOG_GET_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
						"Packet  list has been fetched");
				auditFactory.audit(AuditEvent.PACKET_RETRIVE, Components.PACKET_RETRIVE,
						SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
				details.forEach(detail -> list.add(new RegistrationApprovalDTO(detail.getId(),
						regDateConversion(detail.getCrDtime()), detail.getAckFilename(), RegistrationConstants.EMPTY)));
			} catch (RuntimeException runtimeException) {
				throw new RegBaseUncheckedException(RegistrationConstants.PACKET_RETRIVE_STATUS,
						runtimeException.toString());
			}
		} else {
			throw new RegBaseCheckedException(RegistrationConstants.PACKET_RETRIVE_STATUS_EXCEPTION,
					"Status is empty or null");
		}
		LOGGER.info(LoggerConstants.LOG_GET_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Packets list by status ended");
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.RegistrationApprovalService#packetUpdateStatus(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@PreAuthorizeUserId(roles = { AuthenticationAdvice.OFFICER_ROLE, AuthenticationAdvice.SUPERVISOR_ROLE,
			AuthenticationAdvice.ADMIN_ROLE })
	public Registration updateRegistration(String registrationID, String statusComments, String clientStatusCode)
			throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.LOG_UPADTE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Updating status of Packet");
		auditFactory.audit(AuditEvent.PACKET_UPDATE, Components.PACKET_UPDATE, SessionContext.userContext().getUserId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		Registration registration;
		if (nullCheckForupdateRegistration(registrationID, clientStatusCode)) {
			registration = registrationDAO.updateRegistration(registrationID, statusComments, clientStatusCode);
		} else {
			throw new RegBaseCheckedException(RegistrationConstants.PACKET_UPDATE_STATUS_EXCEPTION,
					"Registartion ID/ Client Status Code is empty or null");
		}
		return registration;
	}

	/**
	 * Null check for getEnrollement by status.
	 *
	 * @param status 
	 * 				the status of packets that needs to be fetched from Database
	 * @return true, 
	 * 				if successful
	 */
	private boolean nullCheckForgetEnrollementByStatus(String status) {

		if (StringUtils.isEmpty(status)) {
			LOGGER.info(LoggerConstants.LOG_GET_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
					"Status is empty or null");
			return false;
		}
		return true;
	}

	/**
	 * Null check for update registration.
	 *
	 * @param registrationID 
	 * 					the registration ID of the registration packet.
	 * @param clientStatusCode 
	 * 					the client status code of the registration packet.
	 * @return true, if successful
	 */
	private boolean nullCheckForupdateRegistration(String registrationID, String clientStatusCode) {

		if (StringUtils.isEmpty(registrationID)) {
			LOGGER.info(LoggerConstants.LOG_GET_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
					"Registration ID is empty or null");
			return false;
		} else if (StringUtils.isEmpty(clientStatusCode)) {
			LOGGER.info(LoggerConstants.LOG_GET_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
					"Client Status Code is empty or null");
			return false;
		} else {
			return true;
		}
	}
}
