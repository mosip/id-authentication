package io.mosip.registration.processor.core.packet.dto.masterdata;

import lombok.Data;

@Data
public class UserDetailsDto {

    private String id;
    private String langCode;
    private String uin;
    private String name;
    private String email;
    private String mobile;
    private String statusCode;
    private Boolean isActive;
}
