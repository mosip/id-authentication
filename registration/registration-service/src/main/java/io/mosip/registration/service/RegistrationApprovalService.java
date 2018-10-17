package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.exception.RegBaseUncheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.entity.Registration;

/**
 * {@code RegistrationApprovalService} is the registration approval service
 * class
 *
 * @author Mahesh Kumar
 */
@Service
public class RegistrationApprovalService {

	/**
	 * Object for Registration DAO
	 */
	@Autowired
	private RegistrationDAO registrationDAO;

	/**
	 * Object for Logger
	 */
	private MosipLogger LOGGER;

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

	/**
	 * {@code getAllEnrollments} method to get the registration packets for approval
	 * 
	 * @return list of packets
	 */
	public List<RegistrationApprovalUiDto> getAllEnrollments() {

		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
				APPLICATION_ID, "Retrieving created Packets list for Approval has been called");
		List<RegistrationApprovalUiDto> list = new ArrayList<>();
		try {
			List<Registration> details = registrationDAO.approvalList();
			LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
					APPLICATION_ID, "Packet Approval list has been fetched");
			auditFactory.audit(AuditEventEnum.PACKET_RETRIVE, AppModuleEnum.PACKET_RETRIVE,
					"Packets which are in created state for approval are retrived", "refId", "refIdType");
			details.forEach(detail -> {
				list.add(new RegistrationApprovalUiDto(detail.getId(), detail.getClientStatusCode(),
						detail.getIndividualName(), detail.getCrBy(), detail.getUserdetail().getName(),
						detail.getAckFilename()));
			});
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_RETRIVE_STATUS,
					runtimeException.toString());
		}
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Packet Approval list has been ended");
		return list;
	}

	/**
	 * {@code getEnrollmentByStatus} method to fetch registration packets on status
	 * basis
	 * 
	 * @param status
	 * @return list of packets
	 */
	public List<Registration> getEnrollmentByStatus(String status) {
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Packets list by status");
		auditFactory.audit(AuditEventEnum.PACKET_RETRIVE, AppModuleEnum.PACKET_RETRIVE,
				"Packets are in retrived based on state", "refId", "refIdType");
		return registrationDAO.getEnrollmentByStatus(status);
	}

	/**
	 * {@code packetUpdateStatus} method to update the status of the packet
	 * 
	 * @param id
	 * @param clientStatusCode
	 * @param approverUserId
	 * @param statusComments
	 * @param updBy
	 * @return Boolean
	 */
	public Boolean packetUpdateStatus(String id, String clientStatusCode, String approverUserId, String statusComments,
			String updBy) {
		LOGGER.debug("REGISTRATION - PACKET - UPDATE", APPLICATION_NAME,
				APPLICATION_ID, "Updating status of Packet");
		auditFactory.audit(AuditEventEnum.PACKET_UPDATE, AppModuleEnum.PACKET_UPDATE,
				"Packets which are in created state are updated according to desired status", "refId", "refIdType");
		return registrationDAO.updateStatus(id, clientStatusCode, approverUserId, statusComments, updBy) != null;
	}

}
