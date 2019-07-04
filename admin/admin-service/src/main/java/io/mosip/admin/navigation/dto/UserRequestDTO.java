package io.mosip.admin.navigation.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author Bal Vikash Sharma
 *
 */
@Data
public class UserRequestDTO {

    @NotBlank
    private String userName;
    @NotBlank
    private String password;
    @NotBlank
    private String appId;

}
