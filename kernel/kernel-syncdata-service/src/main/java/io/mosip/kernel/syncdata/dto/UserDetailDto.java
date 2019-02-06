package io.mosip.kernel.syncdata.dto;

import lombok.Data;

@Data
public class UserDetailDto {

    private String userName;
    private String mobile;
    private String mail;
    private String langCode;
    private byte[] userPassword;
    private String name;
    private String role;

	
}
