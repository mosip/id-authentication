package io.mosip.kernel.auth.entities.otp;

import io.mosip.kernel.auth.entities.MosipUserDto;

public class OtpGenerateRequest {
	private String key;

	public OtpGenerateRequest(MosipUserDto mosipUserDto) {
		this.key = mosipUserDto.getUserId();
	}

	public String getKey() {
		return key;
	}
}
