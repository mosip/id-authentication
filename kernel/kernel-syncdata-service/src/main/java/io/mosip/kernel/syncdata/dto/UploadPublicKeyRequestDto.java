package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model representing a Upload Public Key Request")
public class UploadPublicKeyRequestDto {
	
	@NotBlank
	private String machineName;
	
	@NotBlank
	private String publicKey;

}
