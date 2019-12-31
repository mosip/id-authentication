/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.keymanagerservice.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Model representing a PDF sign request")
public class PDFSignatureRequestDto extends SignatureRequestDto {
	/**
	 * The lower left x value of sign rectangle.
	 */
	@ApiModelProperty(notes = "The lower left x value of sign rectangle.", required = true)
	@Min(value = 0)
	@Max(value = Integer.MAX_VALUE)
	private int lowerLeftX; 
	
	/**
	 * The lower left y value of sign rectangle.
	 */
	@ApiModelProperty(notes = "The lower left y value of sign rectangle.", required = true)
	@Min(value = 0)
	@Max(value = Integer.MAX_VALUE)
	private int lowerLeftY; 
	
	/**
	 * The upper right x value of sign rectangle.
	 */
	@ApiModelProperty(notes = "The upper right x value of sign rectangle.", required = true)
	@Min(value = 0)
	@Max(value = Integer.MAX_VALUE)
	private int upperRightX; 
	
	/**
	 *  The upper right y value of sign rectangle.
	 */
	@ApiModelProperty(notes = "The upper right y value of sign rectangle.", required = true)
	@Min(value = 0)
	@Max(value = Integer.MAX_VALUE)
	private int upperRightY;
	
	/**
	 *  Reason for signing.
	 */
	@ApiModelProperty(notes = "Reason for signing.", required = true)
	@NotBlank(message = KeymanagerConstant.INVALID_REQUEST)
	private String reason;
	
	/**
	 * Page number for signature.
	 */
	@ApiModelProperty(notes = "Page number for signature.", required = true)
	@Min(value = 0)
	@Max(value = Integer.MAX_VALUE)
	private int pageNumber;
	
	/**
	 * Password for protecting PDF
	 */
	@ApiModelProperty(notes = "Password for protecting PDF")
	private String password;
}
