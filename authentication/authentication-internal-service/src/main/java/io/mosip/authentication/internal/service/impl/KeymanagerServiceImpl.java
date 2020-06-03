package io.mosip.authentication.internal.service.impl;

import java.security.KeyPair;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.SignatureCertificate;
import io.mosip.authentication.core.exception.CryptoException;
import io.mosip.authentication.core.exception.NoUniqueAliasException;
import io.mosip.authentication.core.indauth.dto.PublicKeyResponseDto;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.keymanager.service.KeymanagerService;
import io.mosip.authentication.common.service.entity.KeyAlias;
import io.mosip.authentication.common.service.repository.KeyAliasRepository;
import io.mosip.authentication.common.service.repository.KeyStoreRepository;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.keymanager.model.CertificateEntry;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@Component
@Transactional("keymanagerTransactionManager")
public class KeymanagerServiceImpl implements KeymanagerService {
	
	private static final Logger LOGGER = IdaLogger.getLogger(KeymanagerServiceImpl.class);
	
	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	private static final String VALID_REFERENCE_ID_GETTING_KEY_ALIAS_WITH_REFERENCE_ID = "Valid reference Id. Getting key alias with referenceId";

	private static final String NOT_A_VALID_REFERENCE_ID_GETTING_KEY_ALIAS_WITHOUT_REFERENCE_ID = "Not a valid reference Id. Getting key alias without referenceId";
	
	/** The environment. */
	@Autowired
	private Environment env;
	
	/**
	 * {@link KeyAliasRepository} instance
	 */
	@Autowired
	KeyAliasRepository keyAliasRepository;
	
	/**
	 * {@link KeyStoreRepository} instance
	 */
	@Autowired
	KeyStoreRepository keyStoreRepository;
	
	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	private KeyGenerator keyGenerator;
	
	/**
	 * Keystore instance to handles and store cryptographic keys.
	 */
	@Autowired
	private KeyStore keyStore;
	
	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;
	

	@Override
	public PublicKeyResponseDto<String> getPublicKey(String applicationId, String timeStamp, Optional<String> referenceId) {
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.APPLICATIONID, applicationId,
				IdAuthCommonConstants.GETPUBLICKEY);
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.TIMESTAMP, timeStamp.toString(),
				IdAuthCommonConstants.GETPUBLICKEY);
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.REFERENCEID, referenceId.toString(),
				IdAuthCommonConstants.GETPUBLICKEY);
		LocalDateTime localDateTimeStamp = parseToLocalDateTime(timeStamp);		
		PublicKeyResponseDto<String> publicKeyResponse = new PublicKeyResponseDto<>();
		if (!referenceId.isPresent() || referenceId.get().trim().isEmpty()) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
					"Reference Id is not present. Will get public key from SoftHSM");
			PublicKeyResponseDto<PublicKey> hsmPublicKey = getPublicKeyFromHSM(applicationId, localDateTimeStamp);
			publicKeyResponse.setPublicKey(CryptoUtil.encodeBase64(hsmPublicKey.getPublicKey().getEncoded()));
			publicKeyResponse.setIssuedAt(hsmPublicKey.getIssuedAt());
			publicKeyResponse.setExpiryAt(hsmPublicKey.getExpiryAt());
		} else {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
					"Reference Id is present. Will get public key from DB store");
			PublicKeyResponseDto<byte[]> dbPublicKey = getPublicKeyFromDBStore(applicationId, localDateTimeStamp,
					referenceId.get());
			publicKeyResponse.setPublicKey(CryptoUtil.encodeBase64(dbPublicKey.getPublicKey()));
			publicKeyResponse.setIssuedAt(dbPublicKey.getIssuedAt());
			publicKeyResponse.setExpiryAt(dbPublicKey.getExpiryAt());
		}
		return publicKeyResponse;
	}
	
	/**
	 * Function to get public key from DB store. On first request for an
	 * applicationId, referenceId and duration, will create a new keypair.
	 * 
	 * @param applicationId applicationId
	 * @param timeStamp     timeStamp
	 * @param referenceId   referenceId
	 * @return {@link PublicKeyResponse} instance
	 */
	private PublicKeyResponseDto<byte[]> getPublicKeyFromDBStore(String applicationId, LocalDateTime timeStamp,
			String referenceId) {
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.APPLICATIONID, applicationId,
				IdAuthCommonConstants.GETPUBLICKEYDB);
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.TIMESTAMP, timeStamp.toString(),
				IdAuthCommonConstants.GETPUBLICKEYDB);
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.REFERENCEID, referenceId,
				IdAuthCommonConstants.GETPUBLICKEYDB);

		String alias = null;
		byte[] publicKey = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		Map<String, List<KeyAlias>> keyAliasMap = getKeyAliases(applicationId, referenceId, timeStamp);
		List<KeyAlias> currentKeyAlias = keyAliasMap.get(IdAuthCommonConstants.CURRENTKEYALIAS);

		if (currentKeyAlias.size() > 1) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias size more than one. Throwing exception");
			throw new NoUniqueAliasException(IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(),
					"CurrentKeyAlias size is one. Will fetch keypair using this alias");
			Optional<io.mosip.authentication.common.service.entity.KeyStore> keyFromDBStore = keyStoreRepository.findByAlias(currentKeyAlias.get(0).getAlias());
			if (!keyFromDBStore.isPresent()) {
				LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.KEYFROMDB, keyFromDBStore.toString(),
						"Key in DBStore does not exist for this alias. Throwing exception");
				throw new NoUniqueAliasException(IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
						IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
			} else {
				LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.KEYFROMDB,
						currentKeyAlias.get(0).getAlias(), "Key in DBStore exists for this alias. Fetching public key");
				KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
				publicKey = keyFromDBStore.get().getPublicKey();
				generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
				expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
			}
		} else if (currentKeyAlias.isEmpty()) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()),
					"CurrentKeyAlias size is zero. Will create new Keypair for this applicationId, referenceId and timestamp");
			byte[] encryptedPrivateKey;
			alias = UUID.randomUUID().toString();
			KeyPair keypair = keyGenerator.getAsymmetricKey();
			/**
			 * Will get application's master key information from HSM. On first request for
			 * an applicationId and duration, will create a new keypair.
			 */
			PublicKeyResponseDto<PublicKey> hsmPublicKey = getPublicKeyFromHSM(applicationId, timeStamp);
			PublicKey masterPublicKey = hsmPublicKey.getPublicKey();
			String masterAlias = hsmPublicKey.getAlias();
			publicKey = keypair.getPublic().getEncoded();
			generationDateTime = timeStamp;
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime,
					keyAliasMap.get(IdAuthCommonConstants.KEYALIAS));
			/**
			 * Before storing a keypair in db, will first encrypt its private key with
			 * application's master public key from softhsm's keystore
			 */
			try {
				encryptedPrivateKey = encryptKey(keypair.getPrivate(), masterPublicKey);
			} catch (Exception e) {
				throw new CryptoException(IdAuthenticationErrorConstants.CRYPTO_EXCEPTION.getErrorCode(),
						IdAuthenticationErrorConstants.CRYPTO_EXCEPTION.getErrorMessage() + e.getMessage());
			}
			storeKeyInDBStore(alias, masterAlias, keypair.getPublic().getEncoded(), encryptedPrivateKey);
			storeKeyInAlias(applicationId, generationDateTime, referenceId, alias, expiryDateTime);
		}

		return new PublicKeyResponseDto<>(alias, publicKey, generationDateTime, expiryDateTime);

	}


	private SignatureCertificate getSigningCertificate(String applicationId, Optional<String> referenceId,
			String timestamp) {
		String alias = null;
		List<KeyAlias> currentKeyAlias = null;
		Map<String, List<KeyAlias>> keyAliasMap = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		CertificateEntry<X509Certificate, PrivateKey> certificateEntry = null;
		LocalDateTime localDateTimeStamp = parseToLocalDateTime(timestamp);
		if (!referenceId.isPresent() || referenceId.get().trim().isEmpty()) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
					NOT_A_VALID_REFERENCE_ID_GETTING_KEY_ALIAS_WITHOUT_REFERENCE_ID);
			keyAliasMap = getKeyAliases(applicationId, null, localDateTimeStamp);
		} else {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
					VALID_REFERENCE_ID_GETTING_KEY_ALIAS_WITH_REFERENCE_ID);
			keyAliasMap = getKeyAliases(applicationId, referenceId.get(), localDateTimeStamp);
		}
		currentKeyAlias = keyAliasMap.get(IdAuthCommonConstants.CURRENTKEYALIAS);
		if (currentKeyAlias.size() > 1) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias size more than one. Throwing exception");
			throw new NoUniqueAliasException(IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(),
					"CurrentKeyAlias size is one. Will fetch keypair using this alias");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			alias = fetchedKeyAlias.getAlias();
			PrivateKeyEntry privateKeyEntry = keyStore.getAsymmetricKey(alias);
			certificateEntry = new CertificateEntry<>((X509Certificate[]) privateKeyEntry.getCertificateChain(),
					privateKeyEntry.getPrivateKey());
			generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
			expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
		} else if (currentKeyAlias.isEmpty()) {
			throw new NoUniqueAliasException(IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		}
		return new SignatureCertificate(alias, certificateEntry, generationDateTime, expiryDateTime);
	}

	@Override
	public PublicKeyResponseDto<String> getSignPublicKey(String applicationId, String timestamp, Optional<String> referenceId) {
		SignatureCertificate certificateResponse = getSigningCertificate(applicationId, referenceId, timestamp);
		return new PublicKeyResponseDto<>(certificateResponse.getAlias(),
				CryptoUtil.encodeBase64(
						certificateResponse.getCertificateEntry().getChain()[0].getPublicKey().getEncoded()),
				certificateResponse.getIssuedAt(), certificateResponse.getExpiryAt());
	}
	
	/**
	 * Function to get Public key from HSM. On first request for an applicationId
	 * and duration, will create a new keypair.
	 * 
	 * @param applicationId applicationId
	 * @param timeStamp     timeStamp
	 * @return {@link PublicKeyResponse} instance
	 */
	private PublicKeyResponseDto<PublicKey> getPublicKeyFromHSM(String applicationId, LocalDateTime timeStamp) {
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.APPLICATIONID, applicationId,
				IdAuthCommonConstants.GETPUBLICKEYHSM);
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.TIMESTAMP, timeStamp.toString(),
				IdAuthCommonConstants.GETPUBLICKEYHSM);

		String alias = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		Map<String, List<KeyAlias>> keyAliasMap = getKeyAliases(applicationId, null, timeStamp);
		List<KeyAlias> currentKeyAlias = keyAliasMap.get(IdAuthCommonConstants.CURRENTKEYALIAS);

		if (currentKeyAlias.size() > 1) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias size more than one Throwing exception");
			throw new NoUniqueAliasException(IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					IdAuthenticationErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(), "CurrentKeyAlias size is one fetching keypair using this alias");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			alias = fetchedKeyAlias.getAlias();
			generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
			expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
		} else if (currentKeyAlias.isEmpty()) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()),
					"CurrentKeyAlias size is zero. Will create new Keypair for this applicationId and timestamp");
			alias = UUID.randomUUID().toString();
			generationDateTime = timeStamp;
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime,
					keyAliasMap.get(IdAuthCommonConstants.KEYALIAS));
			keyStore.storeAsymmetricKey(keyGenerator.getAsymmetricKey(), alias, generationDateTime, expiryDateTime);
			storeKeyInAlias(applicationId, generationDateTime, null, alias, expiryDateTime);
		}
		return new PublicKeyResponseDto<>(alias, keyStore.getPublicKey(alias), generationDateTime, expiryDateTime);
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
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
				IdAuthCommonConstants.GETALIAS);
		Map<String, List<KeyAlias>> hashmap = new HashMap<>();
		List<KeyAlias> keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId)
				.stream()
				.sorted((alias1, alias2) -> alias1.getKeyGenerationTime().compareTo(alias2.getKeyGenerationTime()))
				.collect(Collectors.toList());
		List<KeyAlias> currentKeyAliases = keyAliases.stream()
				.filter(keyAlias -> isValidTimestamp(timeStamp, keyAlias)).collect(Collectors.toList());
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.KEYALIAS, Arrays.toString(keyAliases.toArray()),
				IdAuthCommonConstants.KEYALIAS);
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.CURRENTKEYALIAS,
				Arrays.toString(currentKeyAliases.toArray()), IdAuthCommonConstants.CURRENTKEYALIAS);
		hashmap.put(IdAuthCommonConstants.KEYALIAS, keyAliases);
		hashmap.put(IdAuthCommonConstants.CURRENTKEYALIAS, currentKeyAliases);
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
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
				IdAuthCommonConstants.STOREKEYALIAS);
		KeyAlias keyAlias = new KeyAlias();
		keyAlias.setAlias(alias);
		keyAlias.setApplicationId(applicationId);
		keyAlias.setReferenceId(referenceId);
		keyAlias.setKeyGenerationTime(timeStamp);
		keyAlias.setKeyExpiryTime(expiryDateTime);
		keyAlias.setCreatedBy("SYSTEM");
		keyAlias.setCreatedtimes(LocalDateTime.now(ZoneId.of("UTC")));
		keyAlias.setIsDeleted(false);
		keyAliasRepository.saveAndFlush(keyAlias);
	}
	
	/**
	 * Function to store key in DB store
	 * 
	 * @param alias               alias
	 * @param masterAlias         masterAlias
	 * @param publicKey           publicKey
	 * @param encryptedPrivateKey encryptedPrivateKey
	 */
	private void storeKeyInDBStore(String alias, String masterAlias, byte[] publicKey, byte[] encryptedPrivateKey) {
		io.mosip.authentication.common.service.entity.KeyStore dbKeyStore = new io.mosip.authentication.common.service.entity.KeyStore();
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
				IdAuthCommonConstants.STOREDBKEY);
		dbKeyStore.setAlias(alias);
		dbKeyStore.setMasterAlias(masterAlias);
		dbKeyStore.setPublicKey(publicKey);
		dbKeyStore.setPrivateKey(encryptedPrivateKey);
		dbKeyStore.setCreatedBy("SYSTEM");
		dbKeyStore.setCreatedtimes(LocalDateTime.now(ZoneId.of("UTC")));
		dbKeyStore.setIsDeleted(false);
		keyStoreRepository.saveAndFlush(dbKeyStore);
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
		LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.APPLICATIONID, applicationId,
				IdAuthCommonConstants.GETEXPIRYPOLICY);		
		long validityInDays = Long.parseLong(env.getProperty(IdAuthConfigKeyConstants.IDA_KEY_VALIDITY_IN_DAYS));
		LocalDateTime policyExpiryTime = timeStamp.plusDays(validityInDays);
		if (!keyAlias.isEmpty()) {
			LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.KEYALIAS, String.valueOf(keyAlias.size()),
					"Getting expiry policy. KeyAlias exists");
			for (KeyAlias alias : keyAlias) {
				if (isOverlapping(timeStamp, policyExpiryTime, alias.getKeyGenerationTime(),
						alias.getKeyExpiryTime())) {
					LOGGER.info(IdAuthCommonConstants.SESSIONID, IdAuthCommonConstants.EMPTY, IdAuthCommonConstants.EMPTY,
							"Overlapping timestamp found. Changing policyExpiryTime");
					policyExpiryTime = alias.getKeyGenerationTime().minusSeconds(1);
					break;
				}
			}
		}
		return policyExpiryTime;
	}

	
	
	/**
	 * Parse a date string of pattern UTC_DATETIME_PATTERN into
	 * {@link LocalDateTime}
	 * 
	 * @param dateTime of type {@link String} of pattern UTC_DATETIME_PATTERN
	 * @return a {@link LocalDateTime} of given pattern
	 */
	private LocalDateTime parseToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
	}
	
	/**
	 * Function to check valid timestamp
	 * 
	 * @param timeStamp timeStamp
	 * @param keyAlias  keyAlias
	 * @return true if timestamp is valid, else false
	 */
	private boolean isValidTimestamp(LocalDateTime timeStamp, KeyAlias keyAlias) {
		return timeStamp.isEqual(keyAlias.getKeyGenerationTime()) || timeStamp.isEqual(keyAlias.getKeyExpiryTime())
				|| (timeStamp.isAfter(keyAlias.getKeyGenerationTime())
						&& timeStamp.isBefore(keyAlias.getKeyExpiryTime()));
	}
	
	/**
	 * Function to check if timestamp is overlapping
	 * 
	 * @param timeStamp         timeStamp
	 * @param policyExpiryTime  policyExpiryTime
	 * @param keyGenerationTime keyGenerationTime
	 * @param keyExpiryTime     keyExpiryTime
	 * @return true if timestamp is overlapping, else false
	 */
	private boolean isOverlapping(LocalDateTime timeStamp, LocalDateTime policyExpiryTime,
			LocalDateTime keyGenerationTime, LocalDateTime keyExpiryTime) {
		return !timeStamp.isAfter(keyExpiryTime) && !keyGenerationTime.isAfter(policyExpiryTime);
	}
	
	/**
	 * Function to encrypt key
	 * 
	 * @param privateKey privateKey
	 * @param masterKey  masterKey
	 * @return encrypted key
	 */
	private byte[] encryptKey(PrivateKey privateKey, PublicKey masterKey) {
		SecretKey symmetricKey = keyGenerator.getSymmetricKey();
		byte[] encryptedPrivateKey = cryptoCore.symmetricEncrypt(symmetricKey, privateKey.getEncoded(), null);
		byte[] encryptedSymmetricKey = cryptoCore.asymmetricEncrypt(masterKey, symmetricKey.getEncoded());
		return CryptoUtil.combineByteArray(encryptedPrivateKey, encryptedSymmetricKey, env.getProperty(IdAuthConfigKeyConstants.IDA_DATA_KEY_SPLITTER));
	}

}
