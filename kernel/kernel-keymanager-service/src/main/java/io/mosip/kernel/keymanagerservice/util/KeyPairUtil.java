/**
 * 
 */
package io.mosip.kernel.keymanagerservice.util;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstants;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.exception.ApplicationIdNotValid;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class KeyPairUtil {

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeyGenerator keyGenerator;
	

	/**
	 * Keystore to handles and store cryptographic keys.
	 */
	@Autowired
	KeyStore keyStore;

	/**
	 * @param applicationId
	 * @param referenceId
	 * @param alias
	 * @param timeStamp 
	 * @param validityInDays 
	 * @return
	 */
	public KeyAlias createNewKeyPair(String applicationId, Optional<String> referenceId, String alias, LocalDateTime timeStamp, int validityInDays) {
		KeyPair keyPair = keyGenerator.getAsymmetricKey();
		keyStore.storeAsymmetricKey(keyPair, alias, 1);
		KeyAlias keyAlias = new KeyAlias();
		keyAlias.setAlias(alias);
		keyAlias.setApplicationId(applicationId);
		if (referenceId.isPresent()) {
			keyAlias.setReferenceId(referenceId.get());
		}
		keyAlias.setKeyGenerationTime(timeStamp);
		keyAlias.setKeyExpiryTime(timeStamp.plusDays(validityInDays));
		return keyAlias;

	}


}
