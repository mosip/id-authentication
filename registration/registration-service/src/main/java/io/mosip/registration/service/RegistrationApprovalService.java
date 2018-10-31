package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
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
	 * {@code getAllEnrollments} method to get the registration packets for approval
	 * 
	 * @return list of packets
	 */
	public List<RegistrationApprovalUiDto> getAllEnrollments() {
		try {
			LOGGER.debug("REGISTRATION - PACKET - RETRIVE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Retrieving created Packets list for Approval has been called");
			List<RegistrationApprovalUiDto> list = new ArrayList<>();
			List<Registration> details = registrationDAO.approvalList();

			LOGGER.debug("REGISTRATION - PACKET - RETRIVE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Packet Approval list has been fetched");

			details.forEach(detail -> {
				try {
					list.add(new RegistrationApprovalUiDto(detail.getId(), detail.getClientStatusCode(),
							detail.getIndividualName(), detail.getCrBy(), detail.getUserdetail().getName(),
							detail.getAckFilename()));

				} catch (RuntimeException runtimeException) {
					throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_RETRIVE_STATUS,
							runtimeException.toString());
				}
			});
			LOGGER.debug("REGISTRATION - PACKET - RETRIVE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Fetching Packet Approval list has been ended");
			return list;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_RETRIVE_STATUS,
					runtimeException.toString());
		}
	}

	/**
	 * {@code getEnrollmentByStatus} method to fetch registration packets on status
	 * basis
	 * 
	 * @param status
	 * @return list of packets
	 */
	public List<Registration> getEnrollmentByStatus(String status) {
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Fetching Packets list by status");
		
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
		LOGGER.debug("REGISTRATION - PACKET - UPDATE", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Updating status of Packet");
		
			return registrationDAO.updateStatus(id, clientStatusCode, approverUserId, statusComments, updBy) != null;
	}

}
