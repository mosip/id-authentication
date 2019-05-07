package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UploadPublicKeyRequestDto {
	
	@NotBlank
	private String machineName;
	
	@NotBlank
	private String publicKey;

}
