package io.mosip.kernel.idgenerator.tokenid.impl;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.idgenerator.tokenid.constant.TokenIDExceptionConstant;
import io.mosip.kernel.idgenerator.tokenid.constant.TokenIdPropertyConstant;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;
import io.mosip.kernel.idgenerator.tokenid.exception.TokenIdGeneratorException;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdRepository;

/**
 * Implementation class for {@link TokenIdGenerator}.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
@Transactional
public class TokenIdGeneratorImpl implements TokenIdGenerator<String> {

	/**
	 * Reference to {@link Encryptor}.
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	@Autowired
	private TokenIdRepository repository;

	/**
	 * The length of Token ID.[fetched from configuration]
	 */
	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator#generateId()
	 */
	@Override
	public String generateId() {
		String counterSecureRandom = null;
		String random = RandomStringUtils.random(
				Integer.parseInt(TokenIdPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
				TokenIdPropertyConstant.ZERO_TO_NINE.getProperty());
		String tokenId = null;
		List<TokenId> listOfEntity = null;
		try {
			listOfEntity = repository.findRandomValues();
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new TokenIdGeneratorException(TokenIDExceptionConstant.TOKENID_FETCH_EXCEPTION.getErrorCode(),
					TokenIDExceptionConstant.TOKENID_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		try {
			TokenId entity = new TokenId();
			if (listOfEntity.isEmpty()) {
				do {
					counterSecureRandom = RandomStringUtils.random(
							Integer.parseInt(TokenIdPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
							TokenIdPropertyConstant.ZERO_TO_NINE.getProperty());
				} while (counterSecureRandom.charAt(0) == '0');
				entity.setRandomValue(random);
				entity.setSequenceCounter(counterSecureRandom);
				repository.save(entity);
			} else {
				counterSecureRandom = listOfEntity.get(0).getSequenceCounter();
				counterSecureRandom = new BigInteger(counterSecureRandom).add(BigInteger.ONE).toString();
				random = listOfEntity.get(0).getRandomValue();
				repository.updateCounterValue(counterSecureRandom, random);
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new TokenIdGeneratorException(TokenIDExceptionConstant.TOKENID_INSERTION_EXCEPTION.getErrorCode(),
					TokenIDExceptionConstant.TOKENID_INSERTION_EXCEPTION.getErrorMessage(), e);
		}

		SecretKey secretKey = new SecretKeySpec(counterSecureRandom.getBytes(),
				TokenIdPropertyConstant.ENCRYPTION_ALGORITHM.getProperty());
		byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, random.getBytes());
		BigInteger bigInteger = new BigInteger(encryptedData);
		tokenId = String.valueOf(bigInteger.abs());
		return tokenId.substring(0, tokenIdLength);
	}
}
