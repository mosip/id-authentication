/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.mosip.kernel.cryptomanager.constant.CryptomanagerConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Class representing a CryptoManager Request")
public class CryptomanagerRequestDto {
/**
 * 
 */
@ApiModelProperty(notes = "Application id of decrypting module", example = "REGISTRATION", required = true)	
@NotBlank(message=CryptomanagerConstant.INVALID_REQUEST)
private String applicationId;
/**
 * 
 */
@ApiModelProperty(notes = "Refrence Id", example = "REF01")
private String referenceId;
/**
 * 
 */
@ApiModelProperty(notes = "Timestamp", example = "2018-12-10T06:12:52.994Z", required = true)
@NotNull
private LocalDateTime timeStamp;
/**
 * 
 */
@ApiModelProperty(notes = "Data in BASE64 encoding to encrypt/decrypt", required = true)
@NotBlank(message=CryptomanagerConstant.INVALID_REQUEST)
private String data; 
}
