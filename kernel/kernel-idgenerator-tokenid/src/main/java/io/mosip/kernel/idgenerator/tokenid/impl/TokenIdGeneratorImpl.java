package io.mosip.kernel.idgenerator.tokenid.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.exception.TokenIdGeneratorException;
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.idgenerator.tokenid.constant.TokenIDExceptionConstant;
import io.mosip.kernel.idgenerator.tokenid.util.TokenIdGeneratorUtil;

/**
 * Implementation class for {@link TokenIdGenerator}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class TokenIdGeneratorImpl implements TokenIdGenerator<String, String> {

	/**
	 * The length of Token ID.[fetched from configuration]
	 */
	@Value("${mosip.kernel.tokenid.length}")
	private Integer tokenIdLength;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator#generateId(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	public String generateId(String tspID, String uin) {
		if (tspID == null || uin == null || tspID.trim().isEmpty() || uin.trim().isEmpty()) {
			throw new TokenIdGeneratorException(TokenIDExceptionConstant.EMPTY_OR_NULL_VALUES.getErrorCode(),
					TokenIDExceptionConstant.EMPTY_OR_NULL_VALUES.getErrorMessage());
		}
		String inputToSHA256Hash = DigestUtils.sha256Hex(tspID + uin);
		String hexToNumericHash = TokenIdGeneratorUtil.encodeHexToNumeric(inputToSHA256Hash);
		return TokenIdGeneratorUtil.compressHash(hexToNumericHash, tokenIdLength);
	}
}
