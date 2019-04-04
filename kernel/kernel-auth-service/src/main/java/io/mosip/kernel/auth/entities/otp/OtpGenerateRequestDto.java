package io.mosip.kernel.auth.entities.otp;

import io.mosip.kernel.auth.entities.MosipUserDto;

public class OtpGenerateRequestDto {
	private String key;

	public OtpGenerateRequestDto(MosipUserDto mosipUserDto) {
		this.key = mosipUserDto.getUserId();
	}

	public String getKey() {
		return key;
	}
}
