package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.List;
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
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstants;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyDbStore;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.exception.CryptoException;
import io.mosip.kernel.keymanagerservice.exception.InvalidApplicationIdException;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
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

	/**
	 * Keystore to handles and store cryptographic keys.
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
	 * 
	 */
	@Autowired
	KeyAliasRepository keyAliasRepository;

	/**
	 * 
	 */
	@Autowired
	KeyPolicyRepository keyPolicyRepository;

	/**
	 * 
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

		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
		if (!referenceId.isPresent() || referenceId.get().trim().isEmpty()) {
			PublicKeyResponse<PublicKey> hsmPublicKey = getPublicKeyFromHSM(applicationId, timeStamp);
			publicKeyResponse.setPublicKey(keymanagerUtil.encodeBase64(hsmPublicKey.getPublicKey().getEncoded()));
			publicKeyResponse.setKeyGenerationTime(hsmPublicKey.getKeyGenerationTime());
			publicKeyResponse.setKeyExpiryTime(hsmPublicKey.getKeyExpiryTime());
		} else {
			PublicKeyResponse<byte[]> dbPublicKey = getPublicKeyFromDBStore(applicationId, timeStamp,
					referenceId.get());
			publicKeyResponse.setPublicKey(keymanagerUtil.encodeBase64(dbPublicKey.getPublicKey()));
			publicKeyResponse.setKeyGenerationTime(dbPublicKey.getKeyGenerationTime());
			publicKeyResponse.setKeyExpiryTime(dbPublicKey.getKeyExpiryTime());
		}
		return publicKeyResponse;
	}

	/**
	 * @param applicationId
	 * @param timeStamp
	 * @param referenceId
	 * @param alias
	 * @param keyResponseDto
	 * @return
	 * @throws NoUniqueAliasException
	 * @throws InvalidApplicationIdException
	 */
	private PublicKeyResponse<PublicKey> getPublicKeyFromHSM(String applicationId, LocalDateTime timeStamp) {

		String alias = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		List<KeyAlias> currentKeyAlias = getCurrentKeyAlias(applicationId, null, timeStamp);

		if (currentKeyAlias.size() > 1) {
			throw new NoUniqueAliasException(KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());

		} else if (currentKeyAlias.size() == 1) {
			System.out.println("!!!Already exists");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			alias = fetchedKeyAlias.getAlias();
			generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
			expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();

		} else if (currentKeyAlias.isEmpty()) {
			System.out.println("!!!Creating new");
			alias = UUID.randomUUID().toString();
			generationDateTime = timeStamp;
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime);
			keyStore.storeAsymmetricKey(keyGenerator.getAsymmetricKey(), alias, generationDateTime, expiryDateTime);
			storeKeyInAlias(applicationId, generationDateTime, null, alias, expiryDateTime);
		}
		return new PublicKeyResponse<>(alias, keyStore.getPublicKey(alias), generationDateTime, expiryDateTime);
	}

	/**
	 * @param applicationId
	 * @param timeStamp
	 * @param referenceId
	 * @return
	 * @throws NoUniqueAliasException
	 */
	private PublicKeyResponse<byte[]> getPublicKeyFromDBStore(String applicationId, LocalDateTime timeStamp,
			String referenceId) {

		String alias = null;
		byte[] keyFromDB = null;
		LocalDateTime generationDateTime = null;
		LocalDateTime expiryDateTime = null;
		List<KeyAlias> currentKeyAlias = getCurrentKeyAlias(applicationId, referenceId, timeStamp);

		if (currentKeyAlias.size() > 1) {
			throw new NoUniqueAliasException(KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());

		} else if (currentKeyAlias.size() == 1) {
			Optional<KeyDbStore> keyFromDBStore = keyStoreRepository.findByAlias(currentKeyAlias.get(0).getAlias());
			if (keyFromDBStore.isPresent()) {
				KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
				keyFromDB = keyFromDBStore.get().getPublicKey();
				generationDateTime = fetchedKeyAlias.getKeyGenerationTime();
				expiryDateTime = fetchedKeyAlias.getKeyExpiryTime();
			} else {
				throw new NoUniqueAliasException(KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
						KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
			}
		} else if (currentKeyAlias.isEmpty()) {
			byte[] encryptedPrivateKey;
			alias = UUID.randomUUID().toString();
			KeyPair keypair = keyGenerator.getAsymmetricKey();
			PublicKeyResponse<PublicKey> hsmPublicKey = getPublicKeyFromHSM(applicationId, timeStamp);
			PublicKey masterPublicKey = hsmPublicKey.getPublicKey();
			String masterAlias = hsmPublicKey.getAlias();

			keyFromDB = keypair.getPublic().getEncoded();
			generationDateTime = timeStamp;
			expiryDateTime = getExpiryPolicy(applicationId, generationDateTime);

			System.out.println(masterPublicKey.toString());
			System.out.println(keypair.getPrivate().toString());

			try {
				encryptedPrivateKey = keymanagerUtil.encryptKey(keypair.getPrivate(), masterPublicKey);
			} catch (InvalidDataException | InvalidKeyException | NullDataException | NullKeyException
					| NullMethodException e) {
				throw new CryptoException(KeymanagerErrorConstants.CRYPTO_EXCEPTION.getErrorCode(),
						KeymanagerErrorConstants.CRYPTO_EXCEPTION.getErrorMessage());
			}
			System.out.println(encryptedPrivateKey.length);

			storeKeyInDBStore(alias, masterAlias, keypair.getPublic().getEncoded(), encryptedPrivateKey);
			storeKeyInAlias(applicationId, generationDateTime, referenceId, alias, expiryDateTime);
		}

		return new PublicKeyResponse<>(alias, keyFromDB, generationDateTime, expiryDateTime);

	}

	/**
	 * @param applicationId
	 * @param string
	 * @param timeStamp
	 * @return
	 */
	private List<KeyAlias> getCurrentKeyAlias(String applicationId, String referenceId, LocalDateTime timeStamp) {

		List<KeyAlias> keyAliases;
		keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId);

		System.out.println("############keyAliases");
		keyAliases.forEach(System.out::println);

		List<KeyAlias> currentKeyAlias = keyAliases
				.stream().filter(
						keyAlias -> timeStamp.isEqual(keyAlias.getKeyGenerationTime())
								|| timeStamp.isEqual(keyAlias.getKeyExpiryTime())
								|| (timeStamp.isAfter(keyAlias.getKeyGenerationTime())
										&& timeStamp.isBefore(keyAlias.getKeyExpiryTime())))
				.collect(Collectors.toList());

		System.out.println("############currentKeyAlias");
		currentKeyAlias.forEach(System.out::println);
		return currentKeyAlias;
	}

	/**
	 * @param applicationId
	 * @param timeStamp
	 * @return
	 * @throws InvalidApplicationIdException
	 */
	private LocalDateTime getExpiryPolicy(String applicationId, LocalDateTime timeStamp) {
		Optional<KeyPolicy> keyPolicy = keyPolicyRepository.findByApplicationId(applicationId);
		if (!keyPolicy.isPresent()) {
			throw new InvalidApplicationIdException(KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorCode(),
					KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorMessage());
		}
		return timeStamp.plusDays(keyPolicy.get().getValidityInDays());
	}

	/**
	 * @param applicationId
	 * @param timeStamp
	 * @param referenceId
	 * @param alias
	 * @param expiryDateTime
	 */
	private void storeKeyInAlias(String applicationId, LocalDateTime timeStamp, String referenceId, String alias,
			LocalDateTime expiryDateTime) {
		KeyAlias keyAlias = new KeyAlias();
		keyAlias.setAlias(alias);
		keyAlias.setApplicationId(applicationId);
		keyAlias.setReferenceId(referenceId);
		keyAlias.setKeyGenerationTime(timeStamp);
		keyAlias.setKeyExpiryTime(expiryDateTime);

		keyAliasRepository.save(keymanagerUtil.setMetaData(keyAlias));
	}

	/**
	 * @param alias
	 * @param bs
	 * @param encryptedPrivateKey
	 */
	private void storeKeyInDBStore(String alias, String masterAlias, byte[] publicKey, byte[] encryptedPrivateKey) {
		KeyDbStore keyDbStore = new KeyDbStore();
		keyDbStore.setAlias(alias);
		keyDbStore.setMasterAlias(masterAlias);
		keyDbStore.setPublicKey(publicKey);
		keyDbStore.setPrivateKey(encryptedPrivateKey);

		keyStoreRepository.save(keymanagerUtil.setMetaData(keyDbStore));
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

		List<KeyAlias> currentKeyAlias;
		LocalDateTime timeStamp = symmetricKeyRequestDto.getTimeStamp();
		String referenceId = symmetricKeyRequestDto.getReferenceId();
		String applicationId = symmetricKeyRequestDto.getApplicationId();
		SymmetricKeyResponseDto keyResponseDto = new SymmetricKeyResponseDto();

		if (referenceId == null || referenceId.trim().isEmpty()) {
			currentKeyAlias = getCurrentKeyAlias(applicationId, null, timeStamp);
		} else {
			currentKeyAlias = getCurrentKeyAlias(applicationId, referenceId, timeStamp);
		}
		
		if (currentKeyAlias.isEmpty() || currentKeyAlias.size() > 1) {

			throw new NoUniqueAliasException(KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {

			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			PrivateKey privateKey = getPrivateKey(referenceId, fetchedKeyAlias);
			System.out.println(privateKey);
			byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey,
					keymanagerUtil.decodeBase64(symmetricKeyRequestDto.getEncryptedSymmetricKey()));
			System.out.println("SymmetricKey: " + decryptedSymmetricKey);
			keyResponseDto.setSymmetricKey(keymanagerUtil.encodeBase64(decryptedSymmetricKey));
		}
		return keyResponseDto;
	}

	/**
	 * @param privateKey
	 * @param referenceId
	 * @param fetchedKeyAlias
	 * @return
	 * @throws CryptoException
	 */
	private PrivateKey getPrivateKey(String referenceId, KeyAlias fetchedKeyAlias) {
		PrivateKey privateKey;
		if (referenceId == null || referenceId.trim().isEmpty()) {
			privateKey = keyStore.getPrivateKey(fetchedKeyAlias.getAlias());
		} else {
			KeyDbStore dbStore = getKeyDbStore(fetchedKeyAlias);
			PrivateKey masterPrivateKey = keyStore.getPrivateKey(dbStore.getMasterAlias());
			try {
				byte[] decryptedPrivateKey = keymanagerUtil.decryptKey(dbStore.getPrivateKey(), masterPrivateKey);
				privateKey = KeyFactory.getInstance("RSA")
						.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKey));
			} catch (InvalidDataException | InvalidKeyException | NullDataException | NullKeyException
					| NullMethodException | InvalidKeySpecException | NoSuchAlgorithmException e) {
				throw new CryptoException(KeymanagerErrorConstants.CRYPTO_EXCEPTION.getErrorCode(),
						KeymanagerErrorConstants.CRYPTO_EXCEPTION.getErrorMessage());
			}
		}
		return privateKey;
	}

	/**
	 * @param fetchedKeyAlias
	 * @return
	 */
	private KeyDbStore getKeyDbStore(KeyAlias fetchedKeyAlias) {
		Optional<KeyDbStore> keyDbStore = keyStoreRepository.findByAlias(fetchedKeyAlias.getAlias());
		if (!keyDbStore.isPresent()) {
			throw new InvalidApplicationIdException(KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorCode(),
					KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorMessage());
		}
		return keyDbStore.get();
	}
}
