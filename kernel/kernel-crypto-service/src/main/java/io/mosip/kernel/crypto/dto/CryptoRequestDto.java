/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.mosip.kernel.crypto.constant.CryptoConstant;
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
public class CryptoRequestDto {
/**
 * 
 */
@NotBlank(message=CryptoConstant.INVALID_REQUEST)
private String applicationId;
/**
 * 
 */
private String referenceId;
/**
 * 
 */
@NotNull
private LocalDateTime timeStamp;
/**
 * 
 */
@NotBlank(message=CryptoConstant.INVALID_REQUEST)
private String data; 
}
