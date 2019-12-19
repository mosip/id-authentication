/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.keymanagerservice.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Crypto-Manager-Request model
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model representing a PDF sign request Request")
public class PDFSignatureRequestDto extends SignatureRequestDto {
	private float x; 
	private float y; 
	private float width; 
	private float height;
	private String reason;
	private int pageNumber;
	private String password;
}
