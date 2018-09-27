package org.mosip.registration.service.packet.encryption.aes.impl;

import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.consts.RegConstants;
import org.mosip.registration.consts.RegProcessorExceptionCode;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import org.mosip.registration.util.mac.SystemMacAddress;
import org.mosip.registration.util.reader.PropertyFileReader;
import org.springframework.stereotype.Component;

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
	 * (non-Javadoc)
	 * @see org.mosip.registration.manager.packet.encryption.aes.AESSeedGenerator#generateAESKeySeeds()
	 */
	@Override
	public List<String> generateAESKeySeeds() throws RegBaseCheckedException {
		try {
			List<String> aesKeySeeds = new LinkedList<>();
			aesKeySeeds.add(SystemMacAddress.getSystemMacAddress());
			aesKeySeeds.add(PropertyFileReader.getPropertyValue(RegConstants.USER_NAME));
			aesKeySeeds.add(String.valueOf(currentTimeMillis()));
			return aesKeySeeds;
		} catch (RegBaseCheckedException checkedException) {
			throw checkedException;
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_SEED_GENERATION, 
					uncheckedException.getMessage());
		}
	}
}
