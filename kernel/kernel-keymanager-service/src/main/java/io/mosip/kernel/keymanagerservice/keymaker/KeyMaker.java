package io.mosip.kernel.keymanagerservice.keymaker;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.KeyMakerSpec;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstant;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.exception.InvalidApplicationIdException;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.logger.KeymanagerLogger;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;

//update logs if changed
//optimize with keymanagerimpl
@Component
public class KeyMaker implements KeyMakerSpec {

	private static final Logger LOGGER = KeymanagerLogger.getLogger(KeyMaker.class);

	// will be taken from property file
	private int symmetricKeyCount = 10000 ;
	
	/**
	 * {@link KeyPolicyRepository} instance
	 */
	@Autowired
	KeyPolicyRepository keyPolicyRepository;
	
	/**
	 * Keystore instance to store keys in softhsm
	 */
	@Autowired
	KeyStore keyStore;

	/**
	 * {@link KeyAliasRepository} instance
	 */
	@Autowired
	KeyAliasRepository keyAliasRepository;

	/**
	 * Utility to generate Metadata
	 */
	@Autowired
	KeymanagerUtil keymanagerUtil;

	@Autowired
	KeyGenerator keyGenerator;

	@Override
	public void createMasterKey(String applicationID) {
		createMasterKeyInHSM(applicationID, DateUtils.getUTCCurrentDateTime());
	}

	/**
	 * Function to get Public key from HSM. On first request for an applicationId
	 * and duration, will create a new keypair.
	 * 
	 * @param applicationId applicationId
	 * @param timeStamp     timeStamp
	 * @return {@link PublicKeyResponse} instance
	 */
	private PublicKeyResponse<PublicKey> createMasterKeyInHSM(String applicationId, LocalDateTime timeStamp) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.APPLICATIONID, applicationId,
				KeymanagerConstant.GETPUBLICKEYHSM);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.TIMESTAMP, timeStamp.toString(),
				KeymanagerConstant.GETPUBLICKEYHSM);

		String alias = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		Map<String, List<KeyAlias>> keyAliasMap = getKeyAliases(applicationId, null, timeStamp);
		List<KeyAlias> currentKeyAlias = keyAliasMap.get(KeymanagerConstant.CURRENTKEYALIAS);

		if (currentKeyAlias.size() > 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias size more than one Throwing exception");
			throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(), "CurrentKeyAlias size is one fetching keypair using this alias");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()),
					"CurrentKeyAlias size is zero. Will create new Keypair for this applicationId and timestamp");
			alias = UUID.randomUUID().toString();
			generationDateTime = expiryDateTime.plusNanos(1);
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime,
					keyAliasMap.get(KeymanagerConstant.KEYALIAS));
			keyStore.storeAsymmetricKey(keyGenerator.getAsymmetricKey(), alias, generationDateTime, expiryDateTime);
			storeKeyInAlias(applicationId, generationDateTime, null, alias, expiryDateTime);
		}
		return new PublicKeyResponse<>(alias, keyStore.getPublicKey(alias), generationDateTime, expiryDateTime);
	}

	/**
	 * Function to get keyalias from keyalias table
	 * 
	 * @param applicationId applicationId
	 * @param referenceId   referenceId
	 * @param timeStamp     timeStamp
	 * @return a map containing a list of all keyalias matching applicationId and
	 *         referenceId with key "keyAlias"; and a list of all keyalias with
	 *         matching timestamp with key "currentKeyAlias"
	 */
	private Map<String, List<KeyAlias>> getKeyAliases(String applicationId, String referenceId,
			LocalDateTime timeStamp) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
				KeymanagerConstant.GETALIAS);
		Map<String, List<KeyAlias>> hashmap = new HashMap<>();
		List<KeyAlias> keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId)
				.stream()
				.sorted((alias1, alias2) -> alias1.getKeyGenerationTime().compareTo(alias2.getKeyGenerationTime()))
				.collect(Collectors.toList());
		List<KeyAlias> currentKeyAliases = keyAliases.stream()
				.filter(keyAlias -> keymanagerUtil.isValidTimestamp(timeStamp, keyAlias)).collect(Collectors.toList());
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.KEYALIAS, Arrays.toString(keyAliases.toArray()),
				KeymanagerConstant.KEYALIAS);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
				Arrays.toString(currentKeyAliases.toArray()), KeymanagerConstant.CURRENTKEYALIAS);
		hashmap.put(KeymanagerConstant.KEYALIAS, keyAliases);
		hashmap.put(KeymanagerConstant.CURRENTKEYALIAS, currentKeyAliases);
		return hashmap;
	}

	/**
	 * Function to store key in keyalias table
	 * 
	 * @param applicationId  applicationId
	 * @param timeStamp      timeStamp
	 * @param referenceId    referenceId
	 * @param alias          alias
	 * @param expiryDateTime expiryDateTime
	 */
	private void storeKeyInAlias(String applicationId, LocalDateTime timeStamp, String referenceId, String alias,
			LocalDateTime expiryDateTime) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
				KeymanagerConstant.STOREKEYALIAS);
		KeyAlias keyAlias = new KeyAlias();
		keyAlias.setAlias(alias);
		keyAlias.setApplicationId(applicationId);
		keyAlias.setReferenceId(referenceId);
		keyAlias.setKeyGenerationTime(timeStamp);
		keyAlias.setKeyExpiryTime(expiryDateTime);
		keyAliasRepository.save(keymanagerUtil.setMetaData(keyAlias));
	}
	
	/**
	 * Function to get expiry datetime using keypolicy table. If a overlapping key
	 * exists for same time interval, then expiry datetime of current key will be
	 * till generation datetime of overlapping key
	 * 
	 * @param applicationId applicationId
	 * @param timeStamp     timeStamp
	 * @param keyAlias      keyAlias
	 * @return expiry datetime
	 */
	private LocalDateTime getExpiryPolicy(String applicationId, LocalDateTime timeStamp, List<KeyAlias> keyAlias) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.APPLICATIONID, applicationId,
				KeymanagerConstant.GETEXPIRYPOLICY);
		Optional<KeyPolicy> keyPolicy = keyPolicyRepository.findByApplicationId(applicationId);
		if (!keyPolicy.isPresent()) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.KEYPOLICY, keyPolicy.toString(),
					"Key Policy not found for this application Id. Throwing exception");
			throw new InvalidApplicationIdException(KeymanagerErrorConstant.APPLICATIONID_NOT_VALID.getErrorCode(),
					KeymanagerErrorConstant.APPLICATIONID_NOT_VALID.getErrorMessage());
		}
		LocalDateTime policyExpiryTime = timeStamp.plusDays(keyPolicy.get().getValidityInDays());
		if (!keyAlias.isEmpty()) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.KEYALIAS, String.valueOf(keyAlias.size()),
					"Getting expiry policy. KeyAlias exists");
			for (KeyAlias alias : keyAlias) {
				if (keymanagerUtil.isOverlapping(timeStamp, policyExpiryTime, alias.getKeyGenerationTime(),
						alias.getKeyExpiryTime())) {
					LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
							"Overlapping timestamp found. Changing policyExpiryTime");
					policyExpiryTime = alias.getKeyGenerationTime().minusSeconds(1);
					break;
				}
			}
		}
		return policyExpiryTime;
	}

	@Override
	public void createSymmetricKeys() {
		

	}

}
