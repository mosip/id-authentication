package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.KeyPair;
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
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.dto.KeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.repository.KeymanagerRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;

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
	 * Keystore to handles and store cryptographic keys.
	 */
	@Autowired
	KeyStore keyStore;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeymanagerRepository keymanagerRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#getPublicKey(java.lang.
	 * String, java.time.LocalDateTime, java.util.Optional)
	 */
	@Override
	public KeyResponseDto getPublicKey(String applicationId, LocalDateTime timeStamp, String machineId) {

		String alias;
		KeyResponseDto keyResponseDto = new KeyResponseDto();
		List<KeyAlias> aliasMaps = keymanagerRepository.findByApplicationIdAndReferenceId(applicationId, machineId);
		aliasMaps.forEach(System.out::println);

		Optional<KeyAlias> currentAliasMap = aliasMaps.stream().sorted(
				(aliasMap1, aliasMap2) -> aliasMap2.getKeyGenerationTime().compareTo(aliasMap1.getKeyGenerationTime()))
				.findFirst();
		System.out.println(currentAliasMap);
		if (!currentAliasMap.isPresent()) {
			System.out.println("!!!Creating new");
			alias = UUID.randomUUID().toString();
			createNewKeyPair(applicationId, machineId, alias);
		} else {
			System.out.println("!!!Already exists");
			alias = currentAliasMap.get().getAlias();
			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
			try {
				certificate.checkValidity();
				System.out.println("!!!Valid");
			} catch (CertificateExpiredException | CertificateNotYetValidException e) {
				System.out.println("!!!Not Valid");
				alias = UUID.randomUUID().toString();
				createNewKeyPair(applicationId, machineId, alias);
			}
		}
		System.out.println(alias);
		PublicKey publicKey = keyStore.getPublicKey(alias);
		keyResponseDto.setKey(publicKey.getEncoded());
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
	public KeyResponseDto decryptSymmetricKey(String applicationId, LocalDateTime timeStamp, String machineId,
			byte[] encryptedSymmetricKey) {

		KeyResponseDto keyResponseDto = new KeyResponseDto();
		List<KeyAlias> aliasMaps = keymanagerRepository.findByApplicationIdAndReferenceId(applicationId, machineId);
		aliasMaps.forEach(System.out::println);

		Optional<KeyAlias> matchingAlias = aliasMaps.stream()
				.filter(aliasMap -> aliasMap.getKeyGenerationTime().compareTo(timeStamp) < 0).sorted((aliasMap1,
						aliasMap2) -> aliasMap2.getKeyGenerationTime().compareTo(aliasMap1.getKeyGenerationTime()))
				.findFirst();

		if (matchingAlias.isPresent()) {
			PrivateKey privateKey = keyStore.getPrivateKey(matchingAlias.get().getAlias());
			System.out.println(matchingAlias.get().getAlias());
			byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey, encryptedSymmetricKey);
			keyResponseDto.setKey(decryptedSymmetricKey);
		}

		return keyResponseDto;
	}

	/**
	 * @param applicationId
	 * @param machineId
	 * @param alias
	 */
	private void createNewKeyPair(String applicationId, String machineId, String alias) {
		KeyPair keyPair = keyGenerator.getAsymmetricKey();
		keyStore.storeAsymmetricKey(keyPair, alias, 1);
		KeyAlias aliasMap = new KeyAlias();
		aliasMap.setAlias(alias);
		aliasMap.setApplicationId(applicationId);
		aliasMap.setReferenceId(machineId);
		aliasMap.setKeyGenerationTime(LocalDateTime.now());
		keymanagerRepository.create(aliasMap);
	}
}
