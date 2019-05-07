package io.mosip.kernel.syncdata.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailMapDto extends BaseDto {

	private String userName;

	private String mail;

	private String mobile;

	private byte[] userPassword;

	private String name;

	private List<String> roles;

	private String rId;

}
