/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.service.impl;

import static java.util.Arrays.copyOfRange;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerAuthRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/**
 * Service Implementation for {@link CryptomanagerService} interface
 * 
 * @author Urvil Joshi
 * @author Srinivasan
 *
 * @since 1.0.0
 */
@Service
public class CryptomanagerServiceImpl implements CryptomanagerService {

	/**
	 * KeySplitter for splitting key and data
	 */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	/**
	 * {@link KeyGenerator} instance
	 */
	@Autowired
	KeyGenerator keyGenerator;

	/**
	 * {@link CryptomanagerUtils} instance
	 */
	@Autowired
	CryptomanagerUtils cryptomanagerUtil;

	/**
	 * {@link Encryptor} instance
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/**
	 * {@link Decryptor} instance
	 */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String, SecureRandom, char[]> cryptoCore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.cryptography.service.CryptographyService#encrypt(io.mosip.
	 * kernel.cryptography.dto.CryptographyRequestDto)
	 */
	@Override
	public CryptomanagerResponseDto encrypt(CryptomanagerRequestDto cryptoRequestDto) {
		SecretKey secretKey = keyGenerator.getSymmetricKey();
		final byte[] encryptedData;
		if (cryptomanagerUtil.isValidSalt(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getSalt()))) {
			encryptedData = encryptor.symmetricEncrypt(secretKey, CryptoUtil.decodeBase64(cryptoRequestDto.getData()),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getSalt())));
		} else {
			encryptedData = encryptor.symmetricEncrypt(secretKey, CryptoUtil.decodeBase64(cryptoRequestDto.getData()));
		}
		PublicKey publicKey = cryptomanagerUtil.getPublicKey(cryptoRequestDto);
		final byte[] encryptedSymmetricKey = encryptor.asymmetricPublicEncrypt(publicKey, secretKey.getEncoded());
		CryptomanagerResponseDto cryptoResponseDto = new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil
				.encodeBase64(CryptoUtil.combineByteArray(encryptedData, encryptedSymmetricKey, keySplitter)));
		return cryptoResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.cryptography.service.CryptographyService#decrypt(io.mosip.
	 * kernel.cryptography.dto.CryptographyRequestDto)
	 */
	@Override
	public CryptomanagerResponseDto decrypt(CryptomanagerRequestDto cryptoRequestDto) {
		int keyDemiliterIndex = 0;
		byte[] encryptedHybridData = CryptoUtil.decodeBase64(cryptoRequestDto.getData());
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(encryptedHybridData, keyDemiliterIndex, keySplitter);
		byte[] encryptedKey = copyOfRange(encryptedHybridData, 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(encryptedHybridData, keyDemiliterIndex + keySplitter.length(),
				encryptedHybridData.length);
		cryptoRequestDto.setData(CryptoUtil.encodeBase64(encryptedKey));
		SecretKey decryptedSymmetricKey = cryptomanagerUtil.getDecryptedSymmetricKey(cryptoRequestDto);
		final byte[] decryptedData;
		if (cryptomanagerUtil.isValidSalt(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getSalt()))) {
			decryptedData = decryptor.symmetricDecrypt(decryptedSymmetricKey, encryptedData,
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getSalt())));
		} else {
			decryptedData = decryptor.symmetricDecrypt(decryptedSymmetricKey, encryptedData);
		}
		CryptomanagerResponseDto cryptoResponseDto = new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(decryptedData));
		return cryptoResponseDto;
	}

	// to be removed after Crypto Core merge with main branch
	@Override
	public CryptomanagerResponseDto authDecrypt(CryptomanagerAuthRequestDto cryptomanagerAuthRequestDto) {
		int keyDemiliterIndex = 0;
		byte[] encryptedHybridData = CryptoUtil.decodeBase64(cryptomanagerAuthRequestDto.getData());
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(encryptedHybridData, keyDemiliterIndex, keySplitter);
		byte[] encryptedKey = copyOfRange(encryptedHybridData, 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(encryptedHybridData, keyDemiliterIndex + keySplitter.length(),
				encryptedHybridData.length);
		cryptomanagerAuthRequestDto.setData(CryptoUtil.encodeBase64(encryptedKey));
		CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto(
				cryptomanagerAuthRequestDto.getApplicationId(), cryptomanagerAuthRequestDto.getReferenceId(),
				cryptomanagerAuthRequestDto.getTimeStamp(), cryptomanagerAuthRequestDto.getData(),
				cryptomanagerAuthRequestDto.getSalt());
		SecretKey decryptedSymmetricKey = cryptomanagerUtil.getDecryptedAuthSymmetricKey(cryptomanagerRequestDto);
		final byte[] decryptedData;
		if (cryptomanagerUtil.isValidSalt(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getSalt()))) {
			decryptedData = cryptoCore.symmetricDecrypt(decryptedSymmetricKey, encryptedData,
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getSalt())),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getAad())));
		} else {
			decryptedData = cryptoCore.symmetricDecrypt(decryptedSymmetricKey, encryptedData,
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getAad())));
		}
		CryptomanagerResponseDto cryptoResponseDto = new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(decryptedData));
		return cryptoResponseDto;
	}

	@Override
	public CryptomanagerResponseDto authEncrypt(CryptomanagerAuthRequestDto cryptomanagerAuthRequestDto) {
		SecretKey secretKey = keyGenerator.getSymmetricKey();
		final byte[] encryptedData;
		if (cryptomanagerUtil.isValidSalt(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getSalt()))) {
			encryptedData = cryptoCore.symmetricEncrypt(secretKey,
					CryptoUtil.decodeBase64(cryptomanagerAuthRequestDto.getData()),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getSalt())),
							CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getAad())));
		} else {
			encryptedData = cryptoCore.symmetricEncrypt(secretKey,
					CryptoUtil.decodeBase64(cryptomanagerAuthRequestDto.getData()),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptomanagerAuthRequestDto.getAad())));
		}
		CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto(
				cryptomanagerAuthRequestDto.getApplicationId(), cryptomanagerAuthRequestDto.getReferenceId(),
				cryptomanagerAuthRequestDto.getTimeStamp(), cryptomanagerAuthRequestDto.getData(),
				cryptomanagerAuthRequestDto.getSalt());
		PublicKey publicKey = cryptomanagerUtil.getPublicKey(cryptomanagerRequestDto);
		final byte[] encryptedSymmetricKey = cryptoCore.asymmetricEncrypt(publicKey, secretKey.getEncoded());
		CryptomanagerResponseDto cryptoResponseDto = new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil
				.encodeBase64(CryptoUtil.combineByteArray(encryptedData, encryptedSymmetricKey, keySplitter)));
		return cryptoResponseDto;
	}

}
