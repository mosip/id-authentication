package io.mosip.kernel.idgenerator.tokenid.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.exception.TokenIdGenerationException;
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.tokenid.cache.TokenIdCacheManager;
import io.mosip.kernel.idgenerator.tokenid.constant.TokenIdGeneratorConstant;
import io.mosip.kernel.idgenerator.tokenid.constant.TokenIdGeneratorErrorCode;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdRepository;
import io.mosip.kernel.idgenerator.tokenid.util.TokenIdFilterUtils;

/**
 * Class generates TokenId 
 * 
 * 
 * @author Srinivasan
 *
 */
@Component
public class TokenIdGeneratorImpl implements TokenIdGenerator<String> {
	/**
	 * Field that takes Integer.This field decides the length of the tokenId. It is
	 * read from the properties file.
	 */
	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	@Autowired
	private TokenIdRepository tokenIdRepository;
	@Autowired
	private TokenIdCacheManager tokenIdCacheManager;

	private int generatedIdLength;

	/**
	 * Decided tokenIdLength based on the value given in the application properties
	 */
	@PostConstruct
	public void tokenIdGeneratorPostConstruct() {
		generatedIdLength = tokenIdLength - 2;
	}

	/**
	 * Method is implementation method of
	 * 
	 * @return tokenId
	 */
	@Override
	public String generateId() {

		boolean unique = false;
		String generatedTokenId = null;
		while (!unique) {
			generatedTokenId = this.generateTokenId();
			if (tokenIdCacheManager.contains(generatedTokenId)) {
				unique = false;
			} else {
				unique = true;
			}
		}
		saveGeneratedTokenId(generatedTokenId);
		return generatedTokenId;

	}

	/**
	 * Method will save tokenId to the database and it will add tokenId to the list.
	 * 
	 * @param generatedTokenId- token id generated from the method
	 * @return string
	 */
	private void saveGeneratedTokenId(String generatedTokenId) {
		long currentTimestamp = System.currentTimeMillis();
		try {
			TokenId tokenId = new TokenId(generatedTokenId, currentTimestamp);
			tokenIdRepository.save(tokenId);
			tokenIdCacheManager.add(tokenId.getId());
		} catch (Exception e) {
			throw new TokenIdGenerationException(TokenIdGeneratorErrorCode.UNABLE_TO_CONNECT_TO_DB.getErrorCode(),
					TokenIdGeneratorErrorCode.UNABLE_TO_CONNECT_TO_DB.getErrorMessage());
		}

	}

	/**
	 * Method generates RandomId. It also validates that the token is generated
	 * based on the rules {@link TokenIdFilterUtils}.If it is not validated then
	 * again another token is generated.
	 * 
	 * @return
	 */
	private String generateTokenId() {

		String generatedTokenId = generateRandomId(generatedIdLength);
		while (!TokenIdFilterUtils.isValidId(generatedTokenId)) {
			generatedTokenId = generateRandomId(generatedIdLength);
		}

		return generatedTokenId;
	}

	/**
	 * Generates a id and then generate checksum
	 * 
	 * @param generatedIdLength -
	 *            The length of id to generate
	 * 
	 * @return the tokenId
	 */
	private String generateRandomId(int generatedIdLength) {
		String generatedTokenId = RandomStringUtils.random(1, TokenIdGeneratorConstant.DIGITS_WITHOUT_ZERO_OR_ONE)
				+ RandomStringUtils.random(generatedIdLength, TokenIdGeneratorConstant.ZERO_TO_NINE);
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(String.valueOf(generatedTokenId));
		return appendChecksum(generatedIdLength, generatedTokenId, verhoeffDigit);

	}

	/**
	 * Method will append checksum to the generated tokenId.
	 * 
	 * @param generatedIdLength 
	 * @param generatedVId
	 * @param verhoeffDigit
	 * @return generatedTokenId
	 */
	private String appendChecksum(int generatedIdLength, String generatedTokenId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(tokenIdLength);
		return vidSb.insert(0, generatedTokenId).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}

}
