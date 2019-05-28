package io.mosip.registration.processor.core.kernel.master.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserResponseDTO {
	/** The id. */
	private String id;
	
	/** The lang code. */
	private String langCode;
	
	/** The uin. */
	private String uin;
	
	/** The name. */
	private String name;
	
	/** The email. */
	private String email;
	
	/** The mobile. */
	private String mobile;
	
	/** The status code. */
	private String statusCode;
	
	/** The is active. */
	private Boolean isActive;
}