package io.mosip.kernel.idgenerator.tokenid.generator;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idgenerator.MosipTokenIdGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;
import io.mosip.kernel.idgenerator.tokenid.cache.TokenIdCacheManager;
import io.mosip.kernel.idgenerator.tokenid.constant.TokenIdGeneratorErrorCodes;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;
import io.mosip.kernel.idgenerator.tokenid.exception.TokenIdGenerationException;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdRepository;
import io.mosip.kernel.idgenerator.tokenid.util.TokenIdFilterUtils;

/**
 * 
 * @author M1046464
 *
 */
@Component
public class TokenIdGenerator implements MosipTokenIdGenerator<String> {

	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	@Autowired
	private TokenIdRepository tokenIdRepository;
	@Autowired
	private TokenIdCacheManager tokenIdCacheManager;

	private int generatedIdLength;

	@PostConstruct
	public void tokenIdGeneratorPostConstruct() {
		generatedIdLength = tokenIdLength - 2;
	}

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
		String genTokenId=saveGeneratedTokenId(generatedTokenId);
		return genTokenId;

	}

	public String saveGeneratedTokenId(String generatedTokenId) {
		long currentTimestamp = System.currentTimeMillis();
		try {
			TokenId tokenId = new TokenId(generatedTokenId,currentTimestamp);
			tokenIdRepository.save(tokenId);
			tokenIdCacheManager.add(tokenId.getId());
			return generatedTokenId;
		} catch (Exception e) {
			throw new TokenIdGenerationException(TokenIdGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorCode(),
					TokenIdGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorMessage());
		}

	}

	private String generateTokenId() {

		String generatedTokenId = generateRandomId(generatedIdLength);
		while (!TokenIdFilterUtils.isValidId(generatedTokenId)) {
			generatedTokenId= generateRandomId(generatedIdLength);
		}
		
		return generatedTokenId;
	}

	/**
	 * Generates a id and then generate checksum
	 * 
	 * @param generatedIdLength
	 *            The length of id to generate
	 * 
	 * @return the tokenId
	 */
	private String generateRandomId(int generatedIdLength) {
		String generatedTokenId=RandomStringUtils.random(1, "23456789") + 
				RandomStringUtils.random(generatedIdLength, "0123456789");
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(String.valueOf(generatedTokenId));
		return appendChecksum(generatedIdLength, generatedTokenId, verhoeffDigit);
		
	}
	
	
	private String appendChecksum(int generatedIdLength, String generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(tokenIdLength);
		return vidSb.insert(0, generatedVId).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}
	
	

}
