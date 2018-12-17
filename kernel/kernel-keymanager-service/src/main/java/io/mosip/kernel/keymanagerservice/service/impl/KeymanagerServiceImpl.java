package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.crypto.exception.NullKeyException;
import io.mosip.kernel.core.crypto.exception.NullMethodException;
import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstant;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.exception.CryptoException;
import io.mosip.kernel.keymanagerservice.exception.InvalidApplicationIdException;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.logger.KeymanagerLogger;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyStoreRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;

/**
 * This class provides the implementation for the methods of KeymanagerService
 * interface.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class KeymanagerServiceImpl implements KeymanagerService {

	private static final Logger LOGGER = KeymanagerLogger.getLogger(KeymanagerServiceImpl.class);

	/**
	 * Keystore instance to handles and store cryptographic keys.
	 */
	@Autowired
	KeyStore keyStore;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeyGenerator keyGenerator;

	/**
	 * Decryptor instance to decrypt data
	 */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/**
	 * {@link KeyAliasRepository} instance
	 */
	@Autowired
	KeyAliasRepository keyAliasRepository;

	/**
	 * {@link KeyPolicyRepository} instance
	 */
	@Autowired
	KeyPolicyRepository keyPolicyRepository;

	/**
	 * {@link KeyStoreRepository} instance
	 */
	@Autowired
	KeyStoreRepository keyStoreRepository;

	/**
	 * Utility to generate Metadata
	 */
	@Autowired
	KeymanagerUtil keymanagerUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#getPublicKey(java.lang.
	 * String, java.time.LocalDateTime, java.util.Optional)
	 */
	@Override
	public PublicKeyResponse<String> getPublicKey(String applicationId, LocalDateTime timeStamp,
			Optional<String> referenceId) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.APPLICATIONID, applicationId,
				KeymanagerConstant.GETPUBLICKEY);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.TIMESTAMP, timeStamp.toString(),
				KeymanagerConstant.GETPUBLICKEY);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.REFERENCEID, referenceId.toString(),
				KeymanagerConstant.GETPUBLICKEY);
		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
		if (!referenceId.isPresent() || referenceId.get().trim().isEmpty()) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
					"Reference Id is not present. Will get public key from SoftHSM");
			PublicKeyResponse<PublicKey> hsmPublicKey = getPublicKeyFromHSM(applicationId, timeStamp);
			publicKeyResponse.setPublicKey(CryptoUtil.encodeBase64(hsmPublicKey.getPublicKey().getEncoded()));
			publicKeyResponse.setIssuedAt(hsmPublicKey.getIssuedAt());
			publicKeyResponse.setExpiryAt(hsmPublicKey.getExpiryAt());
		} else {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
					"Reference Id is present. Will get public key from DB store");
			PublicKeyResponse<byte[]> dbPublicKey = getPublicKeyFromDBStore(applicationId, timeStamp,
					referenceId.get());
			publicKeyResponse.setPublicKey(CryptoUtil.encodeBase64(dbPublicKey.getPublicKey()));
			publicKeyResponse.setIssuedAt(dbPublicKey.getIssuedAt());
			publicKeyResponse.setExpiryAt(dbPublicKey.getExpiryAt());
		}
		return publicKeyResponse;
	}

	/**
	 * Function to get Public key from HSM
	 * 
	 * @param applicationId
	 *            applicationId
	 * @param timeStamp
	 *            timeStamp
	 * @return {@link PublicKeyResponse} instance
	 */
	private PublicKeyResponse<PublicKey> getPublicKeyFromHSM(String applicationId, LocalDateTime timeStamp) {
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
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias size more than one. Throwing exception");
			throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(),
					"CurrentKeyAlias size is one. Will fetch keypair using this alias");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			alias = fetchedKeyAlias.getAlias();
			generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
			expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
		} else if (currentKeyAlias.isEmpty()) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()),
					"CurrentKeyAlias size is zero. Will create new Keypair for this applicationId and timestamp");
			alias = UUID.randomUUID().toString();
			generationDateTime = timeStamp;
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime,
					keyAliasMap.get(KeymanagerConstant.KEYALIAS));
			keyStore.storeAsymmetricKey(keyGenerator.getAsymmetricKey(), alias, generationDateTime, expiryDateTime);
			storeKeyInAlias(applicationId, generationDateTime, null, alias, expiryDateTime);
		}
		return new PublicKeyResponse<>(alias, keyStore.getPublicKey(alias), generationDateTime, expiryDateTime);
	}

	/**
	 * Function to get public key from DB store
	 * 
	 * @param applicationId
	 *            applicationId
	 * @param timeStamp
	 *            timeStamp
	 * @param referenceId
	 *            referenceId
	 * @return {@link PublicKeyResponse} instance
	 */
	private PublicKeyResponse<byte[]> getPublicKeyFromDBStore(String applicationId, LocalDateTime timeStamp,
			String referenceId) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.APPLICATIONID, applicationId,
				KeymanagerConstant.GETPUBLICKEYDB);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.TIMESTAMP, timeStamp.toString(),
				KeymanagerConstant.GETPUBLICKEYDB);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.REFERENCEID, referenceId,
				KeymanagerConstant.GETPUBLICKEYDB);

		String alias = null;
		byte[] publicKey = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		Map<String, List<KeyAlias>> keyAliasMap = getKeyAliases(applicationId, referenceId, timeStamp);
		List<KeyAlias> currentKeyAlias = keyAliasMap.get(KeymanagerConstant.CURRENTKEYALIAS);

		if (currentKeyAlias.size() > 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias size more than one. Throwing exception");
			throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(),
					"CurrentKeyAlias size is one. Will fetch keypair using this alias");
			Optional<io.mosip.kernel.keymanagerservice.entity.KeyStore> keyFromDBStore = keyStoreRepository
					.findByAlias(currentKeyAlias.get(0).getAlias());
			if (!keyFromDBStore.isPresent()) {
				LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.KEYFROMDB, keyFromDBStore.toString(),
						"Key in DBStore does not exist for this alias. Throwing exception");
				throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
						KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
			} else {
				LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.KEYFROMDB,
						currentKeyAlias.get(0).getAlias(), "Key in DBStore exists for this alias. Fetching public key");
				KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
				publicKey = keyFromDBStore.get().getPublicKey();
				generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
				expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
			}
		} else if (currentKeyAlias.isEmpty()) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()),
					"CurrentKeyAlias size is zero. Will create new Keypair for this applicationId, referenceId and timestamp");
			byte[] encryptedPrivateKey;
			alias = UUID.randomUUID().toString();
			KeyPair keypair = keyGenerator.getAsymmetricKey();
			PublicKeyResponse<PublicKey> hsmPublicKey = getPublicKeyFromHSM(applicationId, timeStamp);
			PublicKey masterPublicKey = hsmPublicKey.getPublicKey();
			String masterAlias = hsmPublicKey.getAlias();
			publicKey = keypair.getPublic().getEncoded();
			generationDateTime = timeStamp;
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime,
					keyAliasMap.get(KeymanagerConstant.KEYALIAS));
			try {
				encryptedPrivateKey = keymanagerUtil.encryptKey(keypair.getPrivate(), masterPublicKey);
			} catch (InvalidDataException | InvalidKeyException | NullDataException | NullKeyException
					| NullMethodException e) {
				throw new CryptoException(KeymanagerErrorConstant.CRYPTO_EXCEPTION.getErrorCode(),
						KeymanagerErrorConstant.CRYPTO_EXCEPTION.getErrorMessage() + e.getErrorText());
			}
			storeKeyInDBStore(alias, masterAlias, keypair.getPublic().getEncoded(), encryptedPrivateKey);
			storeKeyInAlias(applicationId, generationDateTime, referenceId, alias, expiryDateTime);
		}

		return new PublicKeyResponse<>(alias, publicKey, generationDateTime, expiryDateTime);

	}

	/**
	 * Function to get key alias
	 * 
	 * @param applicationId
	 *            applicationId
	 * @param referenceId
	 *            referenceId
	 * @param timeStamp
	 *            timeStamp
	 * @return keyalias
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
	 * Function to get Expiry policy
	 * 
	 * @param applicationId
	 *            applicationId
	 * @param timeStamp
	 *            timeStamp
	 * @param keyAlias
	 *            keyAlias
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#decryptSymmetricKey(java
	 * .lang.String, java.time.LocalDateTime, java.util.Optional, byte[])
	 */
	@Override
	public SymmetricKeyResponseDto decryptSymmetricKey(SymmetricKeyRequestDto symmetricKeyRequestDto) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.SYMMETRICKEYREQUEST,
				symmetricKeyRequestDto.toString(), KeymanagerConstant.DECRYPTKEY);

		List<KeyAlias> currentKeyAlias;
		LocalDateTime timeStamp = symmetricKeyRequestDto.getTimeStamp();
		String referenceId = symmetricKeyRequestDto.getReferenceId();
		String applicationId = symmetricKeyRequestDto.getApplicationId();
		SymmetricKeyResponseDto keyResponseDto = new SymmetricKeyResponseDto();

		if (!keymanagerUtil.isValidReferenceId(referenceId)) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
					"Not a valid reference Id. Getting key alias without referenceId");
			currentKeyAlias = getKeyAliases(applicationId, null, timeStamp).get(KeymanagerConstant.CURRENTKEYALIAS);
		} else {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
					"Valid reference Id. Getting key alias with referenceId");
			currentKeyAlias = getKeyAliases(applicationId, referenceId, timeStamp)
					.get(KeymanagerConstant.CURRENTKEYALIAS);
		}

		if (currentKeyAlias.isEmpty() || currentKeyAlias.size() > 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					String.valueOf(currentKeyAlias.size()), "CurrentKeyAlias is not unique. Throwing exception");
			throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.CURRENTKEYALIAS,
					currentKeyAlias.get(0).getAlias(),
					"CurrentKeyAlias size is one. Will decrypt symmetric key for this alias");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			PrivateKey privateKey = getPrivateKey(referenceId, fetchedKeyAlias);
			byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey,
					CryptoUtil.decodeBase64(symmetricKeyRequestDto.getEncryptedSymmetricKey()));
			keyResponseDto.setSymmetricKey(CryptoUtil.encodeBase64(decryptedSymmetricKey));
		}
		return keyResponseDto;
	}

	/**
	 * Function to get Private Key
	 * 
	 * @param referenceId
	 *            referenceId
	 * @param fetchedKeyAlias
	 *            fetchedKeyAlias
	 * @return Private key
	 */
	private PrivateKey getPrivateKey(String referenceId, KeyAlias fetchedKeyAlias) {
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.REFERENCEID, referenceId,
				KeymanagerConstant.GETPRIVATEKEY);
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.FETCHEDKEYALIAS, fetchedKeyAlias.getAlias(),
				KeymanagerConstant.GETPRIVATEKEY);

		if (!keymanagerUtil.isValidReferenceId(referenceId)) {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
					"Not valid reference Id. Getting private key from Keystore");
			return keyStore.getPrivateKey(fetchedKeyAlias.getAlias());
		} else {
			LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
					"Valid reference Id. Getting private key from DB Store");
			Optional<io.mosip.kernel.keymanagerservice.entity.KeyStore> dbKeyStore = keyStoreRepository
					.findByAlias(fetchedKeyAlias.getAlias());
			if (!dbKeyStore.isPresent()) {
				LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.DBKEYSTORE, dbKeyStore.toString(),
						"Key in DB Store does not exists. Throwing exception");
				throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
						KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
			}
			PrivateKey masterPrivateKey = keyStore.getPrivateKey(dbKeyStore.get().getMasterAlias());
			try {
				byte[] decryptedPrivateKey = keymanagerUtil.decryptKey(dbKeyStore.get().getPrivateKey(),
						masterPrivateKey);
				return KeyFactory.getInstance(KeymanagerConstant.RSA)
						.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKey));
			} catch (InvalidDataException | InvalidKeyException | NullDataException | NullKeyException
					| NullMethodException | InvalidKeySpecException | NoSuchAlgorithmException e) {
				throw new CryptoException(KeymanagerErrorConstant.CRYPTO_EXCEPTION.getErrorCode(),
						KeymanagerErrorConstant.CRYPTO_EXCEPTION.getErrorMessage());
			}
		}
	}

	/**
	 * Function to store key in alias
	 * 
	 * @param applicationId
	 *            applicationId
	 * @param timeStamp
	 *            timeStamp
	 * @param referenceId
	 *            referenceId
	 * @param alias
	 *            alias
	 * @param expiryDateTime
	 *            expiryDateTime
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
	 * Function to store key in DB store
	 * 
	 * @param alias
	 *            alias
	 * @param masterAlias
	 *            masterAlias
	 * @param publicKey
	 *            publicKey
	 * @param encryptedPrivateKey
	 *            encryptedPrivateKey
	 */
	private void storeKeyInDBStore(String alias, String masterAlias, byte[] publicKey, byte[] encryptedPrivateKey) {
		io.mosip.kernel.keymanagerservice.entity.KeyStore dbKeyStore = new io.mosip.kernel.keymanagerservice.entity.KeyStore();
		LOGGER.info(KeymanagerConstant.SESSIONID, KeymanagerConstant.EMPTY, KeymanagerConstant.EMPTY,
				KeymanagerConstant.STOREDBKEY);
		dbKeyStore.setAlias(alias);
		dbKeyStore.setMasterAlias(masterAlias);
		dbKeyStore.setPublicKey(publicKey);
		dbKeyStore.setPrivateKey(encryptedPrivateKey);
		keyStoreRepository.save(keymanagerUtil.setMetaData(dbKeyStore));
	}
}