package io.mosip.kernel.auth.dto;

public class MosipUserWithTokenDto {
    private String token;
    private MosipUserDto mosipUserDto;

    public MosipUserWithTokenDto() {}

    public MosipUserWithTokenDto(MosipUserDto mosipUserDto, String token) {
        this.mosipUserDto = mosipUserDto;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MosipUserDto getMosipUserDto() {
        return mosipUserDto;
    }

    public void setMosipUserDto(MosipUserDto mosipUserDto) {
        this.mosipUserDto = mosipUserDto;
    }
}
