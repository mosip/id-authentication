package io.mosip.authentication.fw.dto;

import java.util.Map;

/**
 * The class is to store the generated tokenID for UIN and PartnerId in config
 * file
 * 
 * @author Vignesh
 *
 */
public class TokenIdDto {
	private static Map<String, String> tokenId;

	/**
	 * The method get the generated tokenID from config file
	 * 
	 * @return map - UIN.PartnerID as key, tokenId as value
	 */
	public static Map<String, String> getTokenId() {
		return tokenId;
	}

	/**
	 * The method store the generated tokenID in config file
	 * 
	 * @param map - UIN.PartnerID as key, tokenId as value
	 */
	public static void setTokenId(Map<String, String> tokenId) {
		TokenIdDto.tokenId = tokenId;
	}
}
