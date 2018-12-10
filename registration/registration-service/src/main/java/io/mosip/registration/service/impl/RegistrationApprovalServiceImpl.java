package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RegistrationApprovalService;

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
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationApprovalServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.RegistrationApprovalService#getEnrollmentByStatus(java.lang.String)
	 */
	public List<RegistrationApprovalDTO> getEnrollmentByStatus(String status) {
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching Packets list by status started");
		auditFactory.audit(AuditEvent.PACKET_RETRIVE, AppModule.PACKET_RETRIVE,
				"Packets are in retrived based on state", "refId", "refIdType");

		List<RegistrationApprovalDTO> list = new ArrayList<>();
		try {
			List<Registration> details = registrationDAO.getEnrollmentByStatus(status);
			LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME, APPLICATION_ID,
					"Packet  list has been fetched");
			auditFactory.audit(AuditEvent.PACKET_RETRIVE, AppModule.PACKET_RETRIVE,
					"Packets which are in given state are retrived", "refId", "refIdType");
			details.forEach(detail -> {
				list.add(new RegistrationApprovalDTO(detail.getId(),detail.getAckFilename()));
			});
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_RETRIVE_STATUS,
					runtimeException.toString());
		}
		LOGGER.debug("REGISTRATION - PACKET - RETRIVE", APPLICATION_NAME, APPLICATION_ID,
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
	public Registration updateRegistration(String registrationID, String statusComments, String clientStatusCode) {

		LOGGER.debug("REGISTRATION - PACKET - UPDATE", APPLICATION_NAME, APPLICATION_ID, "Updating status of Packet");
		auditFactory.audit(AuditEvent.PACKET_UPDATE, AppModule.PACKET_UPDATE,
				"Packets which are in created state are updated according to desired status", "refId", "refIdType");
		
		return registrationDAO.updateRegistration(registrationID, statusComments, clientStatusCode);
	}

}
