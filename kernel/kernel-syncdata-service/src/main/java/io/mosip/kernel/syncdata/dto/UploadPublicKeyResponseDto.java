package io.mosip.kernel.syncdata.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model representing a Upload Public Key Response")
public class UploadPublicKeyResponseDto {
	
	private String keyIndex;

}
