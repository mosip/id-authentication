package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_SAVE_PKT;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.constants.RegistrationType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegistrationRepository;

/**
 * The implementation class of {@link RegistrationDAO}.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Repository
@Transactional
public class RegistrationDAOImpl implements RegistrationDAO {

	/** The registration repository. */
	@Autowired
	private RegistrationRepository registrationRepository;

	/** Object for Logger. */
	private static MosipLogger logger;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationDAO#save(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void save(String zipFileName, String individualName) throws RegBaseCheckedException {
		try {
			logger.debug(LOG_SAVE_PKT, APPLICATION_NAME, APPLICATION_ID,
					"Save Registartion had been started");

			OffsetDateTime time = OffsetDateTime.now();

			Registration registration = new Registration();
			registration.setId(zipFileName.substring(zipFileName.lastIndexOf(File.separator) + 1));

			registration.setRegType(RegistrationType.NEW.getCode());
			registration.setRefRegId("12345");
			registration.setStatusCode(RegistrationClientStatusCode.CREATED.getCode());
			registration.setLangCode("ENG");
			registration.setStatusTimestamp(time);
			registration.setAckFilename(zipFileName + "_Ack." + RegistrationConstants.IMAGE_FORMAT);
			registration.setClientStatusCode(RegistrationClientStatusCode.CREATED.getCode());
			// TODO: Get from Session Context - Reg Center
			registration.setIndividualName(individualName);

			registration.setIsActive(true);
			registration.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			registration.setCrDtime(time);

			List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
			RegistrationTransaction registrationTxn = new RegistrationTransaction();
			registrationTxn.setTrnTypeCode(RegistrationTransactionType.CREATED.getCode());
			registrationTxn.setLangCode("ENG");
			registrationTxn.setIsActive(true);
			registrationTxn.setStatusCode(RegistrationClientStatusCode.CREATED.getCode());
			registrationTxn.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			registrationTxn.setCrDtime(time);
			registrationTransactions.add(registrationTxn);
			registration.setRegistrationTransaction(registrationTransactions);

			registrationRepository.create(registration);
			logger.debug(LOG_SAVE_PKT, APPLICATION_NAME, APPLICATION_ID,
					"Save Registration had been ended");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.CREATE_PACKET_ENTITY,
					runtimeException.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationDAO#updateStatus(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Registration updateStatus(String id, String clientStatusCode, String approverUsrId, String statusComments,
			String updBy) {
		try {
			logger.debug("REGISTRATION - UPDATE_STATUS - REGISTRATION_DAO", APPLICATION_NAME,
					APPLICATION_ID, "Packet updation has been started");

			OffsetDateTime timestamp = OffsetDateTime.now();
			Registration registration = registrationRepository.getOne(id);
			registration.setStatusCode(clientStatusCode);
			registration.setStatusTimestamp(timestamp);
			registration.setClientStatusCode(clientStatusCode);
			registration.setClientStatusTimestamp(timestamp);
			registration.setClientStatusComments(statusComments);
			registration.setApproverUsrId(approverUsrId);
			registration.setUpdBy(approverUsrId);
			registration.setUpdDtimes(timestamp);
			List<RegistrationTransaction> registrationTransaction = registration.getRegistrationTransaction();
			RegistrationTransaction registrationTxn = new RegistrationTransaction();
			registrationTxn.setTrnTypeCode(RegistrationTransactionType.UPDATED.getCode());
			registrationTxn.setLangCode("ENG");
			registrationTxn.setIsActive(true);
			registrationTxn.setStatusCode(clientStatusCode);
			registrationTxn.setStatusComment(statusComments);
			registrationTxn.setCrBy(updBy);
			registrationTxn.setCrDtime(timestamp);
			registrationTransaction.add(registrationTxn);

			logger.debug("REGISTRATION - UPDATE_STATUS - REGISTRATION_DAO", APPLICATION_NAME,
					APPLICATION_ID, "Packet updation has been ended");
			return registrationRepository.update(registration);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_UPDATE_STATUS,
					runtimeException.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationDAO#getEnrollmentByStatus(java.lang.
	 * String)
	 */
	@Override
	public List<Registration> getEnrollmentByStatus(String status) {
		logger.debug("REGISTRATION - BY_STATUS - REGISTRATION_DAO", APPLICATION_NAME,
				APPLICATION_ID, "Retriving packets based on status");
		return registrationRepository.findByclientStatusCode(status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationDAO#approvalList()
	 */
	@Override
	public List<Registration> approvalList() {
		logger.debug("REGISTRATION - CREATED_STATUS - REGISTRATION_DAO", APPLICATION_NAME,
				APPLICATION_ID, "Retriving packets based on created status");
		return registrationRepository.findByclientStatusCode(RegistrationClientStatusCode.CREATED.getCode());
	}


	@Override
	public List<Registration> getRegistrationByStatus(List<String> packetStatus) {
		logger.debug("REGISTRATION - GET_PACKET_DETAILS_BY_ID - REGISTRATION_DAO", 
				APPLICATION_NAME, APPLICATION_ID, 
				"got the packet details by id");
		//return registrationRepository.findByClientStatusCodeOrderByCrDtimeAsc(packetStatus);
		return registrationRepository.findByClientStatusCodeIn(packetStatus);
	}
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationDAO#updateRegStatus(java.lang.String)
	 */
	public Registration updateRegStatus(String regId, String status) {
		logger.debug("REGISTRATION - UPDATE_THE_PACKET_STATUS - REGISTRATION_DAO", 
				APPLICATION_NAME, APPLICATION_ID, 
				"Updating the packet details in the Registation table");
		OffsetDateTime timestamp = OffsetDateTime.now();
		
		Registration reg = registrationRepository.getOne(regId);
		if(status.equals("P")) {
			reg.setClientStatusCode("P");
			reg.setFileUploadStatus("S");
		} else {
			reg.setFileUploadStatus("E");
		}
		reg.setIsActive(true);
		reg.setUploadTimestamp(timestamp);
		return registrationRepository.update(reg);
	}

}
