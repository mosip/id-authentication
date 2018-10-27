package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RegistrationApprovalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.entity.Registration;

/**
 * {@code RegistrationApprovalServiceImpl} is the registration approval service
 * class
 *
 * @author Mahesh Kumar
 */
@Service
public class RegistrationApprovalServiceImpl implements RegistrationApprovalService{

	/**
	 * Object for Registration DAO
	 */
	@Autowired
	private RegistrationDAO registrationDAO;

	/**
	 * Object for Logger
	 */
	private static MosipLogger LOGGER;

	/**
	 * Initializing logger
	 * 
	 * @param mosipRollingFileAppender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.RegistrationApprovalService#getAllEnrollments()
	 */
	public List<RegistrationApprovalUiDto> getAllEnrollments() {

		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
				APPLICATION_ID, "Retrieving created Packets list for Approval has been called");
		List<RegistrationApprovalUiDto> list = new ArrayList<>();
		try {
			List<Registration> details = registrationDAO.approvalList();
			LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
					APPLICATION_ID, "Packet Approval list has been fetched");
			auditFactory.audit(AuditEvent.PACKET_RETRIVE, AppModule.PACKET_RETRIVE,
					"Packets which are in created state for approval are retrived", "refId", "refIdType");
			details.forEach(detail -> {
				list.add(new RegistrationApprovalUiDto(detail.getId(), detail.getClientStatusCode(),
						detail.getIndividualName(), detail.getCrBy(), detail.getUserdetail().getName(),
						detail.getAckFilename()));
			});
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_RETRIVE_STATUS,
					runtimeException.toString());
		}
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Packet Approval list has been ended");
		return list;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.RegistrationApprovalService#getEnrollmentByStatus(java.lang.String)
	 */
	public List<Registration> getEnrollmentByStatus(String status) {
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Packets list by status");
		auditFactory.audit(AuditEvent.PACKET_RETRIVE, AppModule.PACKET_RETRIVE,
				"Packets are in retrived based on state", "refId", "refIdType");
		return registrationDAO.getEnrollmentByStatus(status);
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.RegistrationApprovalService#packetUpdateStatus(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Boolean packetUpdateStatus(String id, String clientStatusCode, String approverUserId, String statusComments,
			String updBy) {
		LOGGER.debug("REGISTRATION - PACKET - UPDATE", APPLICATION_NAME,
				APPLICATION_ID, "Updating status of Packet");
		auditFactory.audit(AuditEvent.PACKET_UPDATE, AppModule.PACKET_UPDATE,
				"Packets which are in created state are updated according to desired status", "refId", "refIdType");
		return registrationDAO.updateStatus(id, clientStatusCode, approverUserId, statusComments, updBy) != null;
	}

}
