package io.mosip.kernel.syncdata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailDto extends BaseDto {

	private String userId;
	private String mobile;
	private String mail;
	private byte[] userPassword;
	private String name;
	private String role;
	private String rId;

}
