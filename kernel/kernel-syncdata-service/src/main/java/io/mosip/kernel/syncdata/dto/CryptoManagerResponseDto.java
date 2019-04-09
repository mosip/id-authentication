package io.mosip.kernel.syncdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoManagerResponseDto {
	
	/** Data Encrypted in BASE64 encoding. */
	@ApiModelProperty(notes = "Data encrypted in BASE64 encoding")
	private String data;
}
