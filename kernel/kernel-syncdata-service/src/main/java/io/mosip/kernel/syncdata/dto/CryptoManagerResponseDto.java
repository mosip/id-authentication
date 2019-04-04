package io.mosip.kernel.syncdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoManagerResponseDto {
	
	/** Data Encrypted/Decrypted in BASE64 encoding. */
	@ApiModelProperty(notes = "Data encrypted/decrypted in BASE64 encoding")
	private String data;
}
