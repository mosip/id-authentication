package io.mosip.kernel.auth.entities;

public class OtpTriggerRequestDto {
    private String key;

    public OtpTriggerRequestDto(MosipUser mosipUser) {
        this.key = mosipUser.getUserName().concat(mosipUser.getMail());
    }

    public String getKey() {
        return key;
    }
}
