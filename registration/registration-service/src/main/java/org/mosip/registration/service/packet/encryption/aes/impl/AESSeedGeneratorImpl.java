package org.mosip.registration.service.packet.encryption.aes.impl;

import java.util.LinkedList;
import java.util.List;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import org.mosip.registration.util.mac.SystemMacAddress;
import org.mosip.registration.util.reader.PropertyFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.System.currentTimeMillis;
import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME; 
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;


/**
 * Class for creating the seed values to generate the AES Session Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class AESSeedGeneratorImpl implements AESSeedGenerator {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.manager.packet.encryption.aes.AESSeedGenerator#generateAESKeySeeds()
	 */
	@Override
	public List<String> generateAESKeySeeds() throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - AES_SESSION_KEY_SEEDS", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Generating seeds for AES Encryption had been started");
		try {
			List<String> aesKeySeeds = new LinkedList<>();
			aesKeySeeds.add(SystemMacAddress.getSystemMacAddress());
			aesKeySeeds.add(PropertyFileReader.getPropertyValue(RegConstants.USER_NAME));
			aesKeySeeds.add(String.valueOf(currentTimeMillis()));
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - AES_SESSION_KEY_SEEDS", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Generating seeds for AES Encryption had been ended");
			return aesKeySeeds;
		} catch (RegBaseCheckedException checkedException) {
			throw checkedException;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_SEED_GENERATION,
					runtimeException.toString());
		}
	}
}
