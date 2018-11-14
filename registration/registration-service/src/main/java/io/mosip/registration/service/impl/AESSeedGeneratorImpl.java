package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_AES_SEEDS;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static java.lang.System.currentTimeMillis;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.AESSeedGenerator;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

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
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AESSeedGeneratorImpl.class);

	/**
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.manager.packet.encryption.aes.AESSeedGenerator#generateAESKeySeeds()
	 */
	@Override
	public List<String> generateAESKeySeeds() throws RegBaseCheckedException {
		LOGGER.debug(LOG_PKT_AES_SEEDS, APPLICATION_NAME, APPLICATION_ID,
				"Generating seeds for AES Encryption had been started");
		try {
			List<String> aesKeySeeds = new LinkedList<>();
			aesKeySeeds.add(RegistrationSystemPropertiesChecker.getMachineId());
			aesKeySeeds.add(SessionContext.getInstance().getUserContext().getName());
			aesKeySeeds.add(String.valueOf(currentTimeMillis()));
			
			LOGGER.debug(LOG_PKT_AES_SEEDS, APPLICATION_NAME, APPLICATION_ID,
					"Generating seeds for AES Encryption had been ended");
			
			return aesKeySeeds;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.AES_SEED_GENERATION,
					runtimeException.toString());
		}
	}
}
