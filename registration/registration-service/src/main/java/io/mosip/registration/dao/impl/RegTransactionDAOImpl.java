package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.dao.RegTransactionDAO;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.repositories.RegTransactionRepository;

@Repository
public class RegTransactionDAOImpl implements RegTransactionDAO {

	@Autowired
	private RegTransactionRepository regTransactionRepository;
	/**
	 * Object for Logger
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	public RegistrationTransaction buildRegTrans(String regId, String statusCode) {
		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - REGISTRATION_TRANSACTION_DAO",
				APPLICATION_NAME, APPLICATION_ID,
				"Packet encryption had been ended");

		OffsetDateTime time = OffsetDateTime.now();
		RegistrationTransaction regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId(regId);
		regTransaction.setIsActive(true);
		regTransaction.setTrnTypeCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
		regTransaction.setStatusCode(statusCode);
		regTransaction.setCrBy("mosip");
		regTransaction.setCrDtime(time);

		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - REGISTRATION_TRANSACTION_DAO",
				APPLICATION_NAME, APPLICATION_ID,
				"Packet encryption had been ended");

		return regTransaction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegTransactionDAO#insertPacketTransDetails(java.
	 * util.List)
	 */
	public List<RegistrationTransaction> insertPacketTransDetails(
			List<RegistrationTransaction> registrationTransactions) {
		LOGGER.debug("REGISTRATION - INSERT_PACKET_TRANSACTION_DETAILS - REG_TRANSACTION_DAO",
				APPLICATION_NAME, APPLICATION_ID,
				"Inserting the packet status details in the transaction table");
		return regTransactionRepository.saveAll(registrationTransactions);
	}
}
