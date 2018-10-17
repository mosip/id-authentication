package io.mosip.registration.service.packet.encryption.aes.impl;

import java.util.LinkedList;
import java.util.List;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import io.mosip.registration.util.mac.SystemMacAddress;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static java.lang.System.currentTimeMillis;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_AES_SEEDS;

/**
 * Class for creating the seed values to generate the AES Session Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AESSeedGeneratorImpl implements AESSeedGenerator {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.manager.packet.encryption.aes.AESSeedGenerator#generateAESKeySeeds()
	 */
	@Override
	public List<String> generateAESKeySeeds() throws RegBaseCheckedException {
		logger.debug(LOG_PKT_AES_SEEDS, APPLICATION_NAME, APPLICATION_ID,
				"Generating seeds for AES Encryption had been started");
		try {
			List<String> aesKeySeeds = new LinkedList<>();
			aesKeySeeds.add(SystemMacAddress.getSystemMacAddress());
			aesKeySeeds.add(SessionContext.getInstance().getUserContext().getName());
			aesKeySeeds.add(String.valueOf(currentTimeMillis()));
			logger.debug(LOG_PKT_AES_SEEDS, APPLICATION_NAME, APPLICATION_ID,
					"Generating seeds for AES Encryption had been ended");
			return aesKeySeeds;
		} catch (RegBaseCheckedException checkedException) {
			throw checkedException;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_SEED_GENERATION,
					runtimeException.toString());
		}
	}
}
