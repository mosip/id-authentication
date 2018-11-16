/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.dto;

import java.time.LocalDateTime;
import java.util.Optional;

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
private String appId;
/**
 * 
 */
private Optional<String> machineId;
/**
 * 
 */
private LocalDateTime timeStamp;
/**
 * 
 */
private byte[] data;
}
