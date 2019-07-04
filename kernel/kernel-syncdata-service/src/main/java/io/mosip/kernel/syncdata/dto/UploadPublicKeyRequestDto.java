package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotBlank;

import io.mosip.kernel.syncdata.constant.SyncDataConstant;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model representing a Upload Public Key Request")
public class UploadPublicKeyRequestDto {
	
	@NotBlank(message = SyncDataConstant.INVALID_REQUEST)
	private String machineName;
	
	@NotBlank(message = SyncDataConstant.INVALID_REQUEST)
	private String publicKey;

}
