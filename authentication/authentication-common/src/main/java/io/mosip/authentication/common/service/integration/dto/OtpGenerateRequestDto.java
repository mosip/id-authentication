package io.mosip.authentication.common.service.integration.dto;

public class OtpGenerateRequestDto {
	private String key;

	public OtpGenerateRequestDto(String userId) {
		this.key = userId;
	}

	public String getKey() {
		return key;
	}
}
