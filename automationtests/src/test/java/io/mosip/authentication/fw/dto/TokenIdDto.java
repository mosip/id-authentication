package io.mosip.authentication.fw.dto;

import java.util.Map;

public class TokenIdDto {
	private static Map<String,String> tokenId;

	public static Map<String, String> getTokenId() {
		return tokenId;
	}

	public static void setTokenId(Map<String, String> tokenId) {
		TokenIdDto.tokenId = tokenId;
	}
}
