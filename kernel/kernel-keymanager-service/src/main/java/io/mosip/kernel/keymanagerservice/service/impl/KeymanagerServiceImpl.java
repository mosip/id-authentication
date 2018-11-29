package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstants;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponseDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.exception.ApplicationIdNotValidException;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.keymanagerservice.util.KeyPairUtil;
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
	 * Utility to generate KeyPair
	 */
	@Autowired
	KeyPairUtil keyPairUtil;

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

		String alias;
		List<KeyAlias> keyAliases;
		PublicKeyResponseDto keyResponseDto = new PublicKeyResponseDto();

		if (referenceId.isPresent()) {
			keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId.get());
		} else {
			keyAliases = keyAliasRepository.findByApplicationId(applicationId);
		}

		keyAliases.forEach(System.out::println);

		Optional<KeyAlias> currentKeyAlias = keyAliases.stream().sorted(
				(keyAlias1, keyAlias2) -> keyAlias2.getKeyGenerationTime().compareTo(keyAlias1.getKeyGenerationTime()))
				.findFirst();

		System.out.println(currentKeyAlias);

		if (!currentKeyAlias.isPresent()) {

			System.out.println("!!!Creating new");
			alias = UUID.randomUUID().toString();
			System.out.println(applicationId);
			KeyAlias keyAlias = getKeyPolicy(applicationId, timeStamp,referenceId, alias);
			keyAliasRepository.create(metadataUtil.setMetaData(keyAlias));
		} else {

			System.out.println("!!!Already exists");
			alias = currentKeyAlias.get().getAlias();
			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
			try {

				certificate.checkValidity();
				System.out.println("!!!Valid");
			} catch (CertificateExpiredException | CertificateNotYetValidException e) {

				System.out.println("!!!Not Valid");
				alias = UUID.randomUUID().toString();
				KeyAlias keyAlias = getKeyPolicy(applicationId, timeStamp,referenceId, alias);
				keyAliasRepository.create(metadataUtil.setMetaData(keyAlias));
			}
		}
		System.out.println(alias);
		PublicKey publicKey = keyStore.getPublicKey(alias);
		keyResponseDto.setPublicKey(publicKey.getEncoded());
		return keyResponseDto;
	}

	private KeyAlias getKeyPolicy(String applicationId, LocalDateTime timeStamp,
			Optional<String> referenceId, String alias) {
		Optional<KeyPolicy> keyPolicy=keyPolicyRepository.findByApplicationId(applicationId);
		if(!keyPolicy.isPresent())
			throw new ApplicationIdNotValidException(KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorCode(), KeymanagerErrorConstants.APPLICATIONID_NOT_VALID.getErrorMessage());
		return  keyPairUtil.createNewKeyPair(applicationId, referenceId, alias,timeStamp,keyPolicy.get().getValidityInDays());
	
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

		SymmetricKeyResponseDto keyResponseDto = new SymmetricKeyResponseDto();
		List<KeyAlias> keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(
				symmetricKeyRequestDto.getApplicationId(), symmetricKeyRequestDto.getReferenceId());
		keyAliases.forEach(System.out::println);

		Optional<KeyAlias> matchingAlias = keyAliases.stream().filter(
				keyAlias -> keyAlias.getKeyGenerationTime().compareTo(symmetricKeyRequestDto.getTimeStamp()) < 0)
				.sorted((keyAlias1, keyAlias2) -> keyAlias2.getKeyGenerationTime()
						.compareTo(keyAlias1.getKeyGenerationTime()))
				.findFirst();

		if (matchingAlias.isPresent()) {
			PrivateKey privateKey = keyStore.getPrivateKey(matchingAlias.get().getAlias());
			System.out.println(matchingAlias.get().getAlias());
			byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey,
					symmetricKeyRequestDto.getEncryptedSymmetricKey());
			
			keyResponseDto.setSymmetricKey(decryptedSymmetricKey);
		}
		System.out.println("decryp"+keyResponseDto.getSymmetricKey());
		return keyResponseDto;
	}
}
