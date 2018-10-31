package io.mosip.registration.service.packet.encryption.aes.impl;

import java.util.LinkedList;
import java.util.List;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import io.mosip.registration.util.mac.SystemMacAddress;
import io.mosip.registration.util.reader.PropertyFileReader;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;
import static java.lang.System.currentTimeMillis;


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
	 * @see io.mosip.registration.manager.packet.encryption.aes.AESSeedGenerator#generateAESKeySeeds()
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
