package io.mosip.admin.navigation.dto;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author Ayush Saxena
 *
 */
@Data
public class OtpUserDTO {
	
	@NotBlank
	private String userId;
	@NotNull
	private List<String> otpChannel;
	@NotBlank
	private String appId;
	@NotBlank
	private String useridtype;
	@NotNull
	private Map<String,Object> templateVariables;
	@NotBlank
	private String context;

}
