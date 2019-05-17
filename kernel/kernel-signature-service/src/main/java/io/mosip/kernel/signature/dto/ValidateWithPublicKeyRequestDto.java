package io.mosip.kernel.signature.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateWithPublicKeyRequestDto {
	@NotBlank
	private String responseSignature;
	@NotBlank
	private String responseBody;
	@NotBlank
	private String publicKey;

}
