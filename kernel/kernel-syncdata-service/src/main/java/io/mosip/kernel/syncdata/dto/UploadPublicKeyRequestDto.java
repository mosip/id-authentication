package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPublicKeyRequestDto {
	
	@NotBlank
	private String machineName;
	
	@NotBlank
	private String publicKey;

}
