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

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.util.CryptoUtil;
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
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

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
			encryptedData = cryptoCore.symmetricEncrypt(secretKey,
					CryptoUtil.decodeBase64(cryptoRequestDto.getData()),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getSalt())),
							CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getAad())));
		} else {
			encryptedData = cryptoCore.symmetricEncrypt(secretKey,
					CryptoUtil.decodeBase64(cryptoRequestDto.getData()),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getAad())));
		}
		PublicKey publicKey = cryptomanagerUtil.getPublicKey(cryptoRequestDto);
		final byte[] encryptedSymmetricKey = cryptoCore.asymmetricEncrypt(publicKey, secretKey.getEncoded());
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
			decryptedData = cryptoCore.symmetricDecrypt(decryptedSymmetricKey, encryptedData,
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getSalt())),
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getAad())));
		} else {
			decryptedData = cryptoCore.symmetricDecrypt(decryptedSymmetricKey, encryptedData,
					CryptoUtil.decodeBase64(CryptomanagerUtils.nullOrTrim(cryptoRequestDto.getAad())));
		}
		CryptomanagerResponseDto cryptoResponseDto = new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(decryptedData));
		return cryptoResponseDto;
	}

}
