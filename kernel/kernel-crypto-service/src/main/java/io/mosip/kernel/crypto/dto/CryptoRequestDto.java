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
@NotBlank
private String applicationId;
/**
 * 
 */
private String machineId;
/**
 * 
 */
@NotNull
private LocalDateTime timeStamp;
/**
 * 
 */
@NotNull
private byte[] data; 
}
