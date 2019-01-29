package io.mosip.kernel.auth.entities.otp;

import io.mosip.kernel.auth.entities.MosipUser;

public class OtpGenerateRequestDto {
    private String key;

    public OtpGenerateRequestDto(MosipUser mosipUser) {
        this.key = mosipUser.getUserName().concat(mosipUser.getMail());
    }

    public String getKey() {
        return key;
    }
}
