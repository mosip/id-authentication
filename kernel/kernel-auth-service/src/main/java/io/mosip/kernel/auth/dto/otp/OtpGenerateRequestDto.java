package io.mosip.kernel.auth.dto.otp;

import io.mosip.kernel.auth.dto.MosipUserDto;

public class OtpGenerateRequestDto {
    private String key;

    public OtpGenerateRequestDto(MosipUserDto mosipUserDto) {
        this.key = mosipUserDto.getUserName().concat(mosipUserDto.getMail());
    }

    public String getKey() {
        return key;
    }
}
