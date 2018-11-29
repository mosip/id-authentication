package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstants;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponseDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.exception.InvalidApplicationIdException;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.keymanagerservice.util.MetadataUtil;

/**
 * This class provides the implementation for the methods of KeymanagerService
 * interface.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
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
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeyAliasRepository keyAliasRepository;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeyPolicyRepository keyPolicyRepository;

	/**
	 * Utility to generate Metadata
	 */
	@Autowired
	MetadataUtil metadataUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#getPublicKey(java.lang.
	 * String, java.time.LocalDateTime, java.util.Optional)
	 */
	public PublicKeyResponseDto getPublicKey(String applicationId, LocalDateTime timeStamp,
			Optional<String> referenceId) {

		String alias = null;
		List<KeyAlias> keyAliases;
		PublicKeyResponseDto keyResponseDto = new PublicKeyResponseDto();

		if (referenceId.isPresent()) {
			keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId.get());
		} else {
			keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, null);
		}

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

		if (currentKeyAlias.size() > 1) {

			throw new NoUniqueAliasException(KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {

			System.out.println("!!!Already exists");
			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);
			alias = fetchedKeyAlias.getAlias();
			keyResponseDto.setKeyGenerationTime(fetchedKeyAlias.getKeyGenerationTime());
			keyResponseDto.setKeyExpiryTime(fetchedKeyAlias.getKeyExpiryTime());

		} else if (currentKeyAlias.isEmpty()) {

			System.out.println("!!!Creating new");
			alias = UUID.randomUUID().toString();

			Optional<KeyPolicy> keyPolicy = keyPolicyRepository.findByApplicationId(applicationId);
			if (!keyPolicy.isPresent()) {
				throw new InvalidApplicationIdException(KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorCode(),
						KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorMessage());
			}

			LocalDateTime expiryDateTime = timeStamp.plusDays(keyPolicy.get().getValidityInDays());

			KeyPair keyPair = keyGenerator.getAsymmetricKey();
			keyStore.storeAsymmetricKey(keyPair, alias, timeStamp, expiryDateTime);

			keyResponseDto.setKeyGenerationTime(timeStamp);
			keyResponseDto.setKeyExpiryTime(expiryDateTime);

			KeyAlias keyAlias = new KeyAlias();
			keyAlias.setAlias(alias);
			keyAlias.setApplicationId(applicationId);
			if (referenceId.isPresent()) {
				keyAlias.setReferenceId(referenceId.get());
			} else {
				keyAlias.setReferenceId(null);
			}
			keyAlias.setKeyGenerationTime(timeStamp);
			keyAlias.setKeyExpiryTime(expiryDateTime);

			keyAliasRepository.create(metadataUtil.setMetaData(keyAlias));
		}
		System.out.println(alias);
		PublicKey publicKey = keyStore.getPublicKey(alias);
		// System.out.println(keyStore.getCertificate(alias).toString());
		keyResponseDto.setPublicKey(Base64.encodeBase64URLSafeString(publicKey.getEncoded()));
		return keyResponseDto;
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

		List<KeyAlias> keyAliases;
		SymmetricKeyResponseDto keyResponseDto = new SymmetricKeyResponseDto();
		LocalDateTime timeStamp = symmetricKeyRequestDto.getTimeStamp();
		String referenceId = symmetricKeyRequestDto.getReferenceId();
		String applicationId = symmetricKeyRequestDto.getApplicationId();

		if (referenceId == null) {
			keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, null);
		} else {
			keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId);
		}

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

		if (currentKeyAlias.isEmpty() || currentKeyAlias.size() > 1) {

			throw new NoUniqueAliasException(KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorCode(),
					KeymanagerErrorConstants.NO_UNIQUE_ALIAS.getErrorMessage());
		} else if (currentKeyAlias.size() == 1) {

			KeyAlias fetchedKeyAlias = currentKeyAlias.get(0);

			PrivateKey privateKey = keyStore.getPrivateKey(fetchedKeyAlias.getAlias());
			System.out.println(privateKey);

			byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey,
					Base64.decodeBase64(symmetricKeyRequestDto.getEncryptedSymmetricKey()));

			System.out.println("SymmetricKey: " + decryptedSymmetricKey);
			keyResponseDto.setSymmetricKey(Base64.encodeBase64URLSafeString(decryptedSymmetricKey));

		}
		return keyResponseDto;
	}
}
