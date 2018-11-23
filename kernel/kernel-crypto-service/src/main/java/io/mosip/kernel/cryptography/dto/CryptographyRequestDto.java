/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.dto;

import java.time.LocalDateTime;

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
public class CryptographyRequestDto {
/**
 * 
 */
//@NotBlank
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
