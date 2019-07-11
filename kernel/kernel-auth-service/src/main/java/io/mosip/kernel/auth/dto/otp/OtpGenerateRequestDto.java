package io.mosip.kernel.auth.dto.otp;

import io.mosip.kernel.auth.dto.MosipUserDto;

public class OtpGenerateRequestDto {
	private String key;

	public OtpGenerateRequestDto(MosipUserDto mosipUserDto) {
		this.key = mosipUserDto.getUserId();
	}

	public String getKey() {
		return key;
	}
}
