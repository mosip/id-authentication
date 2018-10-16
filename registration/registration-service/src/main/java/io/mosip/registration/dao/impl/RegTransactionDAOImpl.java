package io.mosip.registration.dao.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegClientStatusCode;
import io.mosip.registration.dao.RegTransactionDAO;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.repositories.RegTransactionRepository;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * DAO class for the {@link RegistrationTransaction} entity
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Repository
public class RegTransactionDAOImpl implements RegTransactionDAO {

	@Autowired
	private RegTransactionRepository regTransactionRepository;
	/**
	 * Object for Logger
	 */
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	public RegistrationTransaction buildRegTrans(String regId) {
		logger.debug("REGISTRATION - PACKET_ENCRYPTION - REGISTRATION_TRANSACTION_DAO",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Packet encryption had been ended");

		OffsetDateTime time = OffsetDateTime.now();
		RegistrationTransaction regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId(regId);
		regTransaction.setTrnTypeCode(RegClientStatusCode.CREATED.getCode());
		regTransaction.setStatusCode(RegClientStatusCode.CREATED.getCode());
		regTransaction.setCrBy("mosip");
		regTransaction.setCrDtime(time);

		logger.debug("REGISTRATION - PACKET_ENCRYPTION - REGISTRATION_TRANSACTION_DAO",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
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
		logger.debug("REGISTRATION - INSERT_PACKET_TRANSACTION_DETAILS - REG_TRANSACTION_DAO",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Inserting the packet status details in the transaction table");
		return regTransactionRepository.saveAll(registrationTransactions);
	}
}
